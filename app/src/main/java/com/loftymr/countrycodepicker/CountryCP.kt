package com.loftymr.countrycodepicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loftymr.countrycodepicker.Country.Companion.countryList
import com.loftymr.countrycodepicker.utils.CCPLaunchedEffect
import com.loftymr.countrycodepicker.utils.EMPTY_STRING
import com.loftymr.countrycodepicker.utils.PhoneNumberTransformation
import com.loftymr.countrycodepicker.utils.clickableNoRipple
import com.loftymr.countrycodepicker.utils.getCountryFlags
import com.loftymr.countrycodepicker.utils.mergeNameAndCode
import java.util.Locale

private var fullNumberState: String by mutableStateOf(EMPTY_STRING)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CountryCP(
    modifier: Modifier = Modifier,
    text: String = EMPTY_STRING,
    onValueChange: (String) -> Unit = {},
    onFullNumberValue: (String) -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    showCountryCode: Boolean = true,
    showError: Boolean = false,
    errorText: String? = null,
    showClearIcon: Boolean = true,
    focusField: Boolean = false,
    errorTextPaddings: PaddingValues = PaddingValues(horizontal = 16.dp),
    phonePlaceholder: @Composable ((txtPlaceHolder: String?) -> Unit) = { txtPlaceHolder ->
        Text(
            text = txtPlaceHolder.orEmpty(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraLight
            ),
        )
    },
    searchPlaceholder: @Composable ((txtPlaceHolder: String?) -> Unit) = { txtPlaceHolder ->
        Text(
            text = txtPlaceHolder.orEmpty(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraLight
            ),
        )
    },
    onClicked: (() -> Unit)? = null,
    phoneFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    searchFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    customRightIcon: @Composable (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = remember { mutableStateOf(false) }

    var textFieldValue by remember { mutableStateOf(EMPTY_STRING) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPickCountry by remember { mutableStateOf(Country.countryList.first()) }
    var phoneCode by remember { mutableStateOf(isPickCountry.countryPhoneCode) }
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = EMPTY_STRING)
    fullNumberState = phoneCode + textFieldValue

    fun shouldShowClearTrailingIcon() = text.isEmpty().not() && isFocused.value && showClearIcon
    fun shouldShowTrailingIcon() =
        showError || shouldShowClearTrailingIcon() || customRightIcon != null

    CCPLaunchedEffect(fullNumberState) {
        onFullNumberValue.invoke(fullNumberState)
    }

    CCPLaunchedEffect(focusField) {
        if (focusField) {
            focusRequester.requestFocus()
        }
    }

    CCPLaunchedEffect(interactionSource.interactions) {
        onClicked?.let {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    it.invoke()
                }
            }
        }
    }

    Box {
        Column {
            OutlinedTextField(
                modifier = modifier,
                shape = shape,
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    if (text != it) {
                        onValueChange(it)
                    }
                },
                singleLine = true,
                colors = phoneFieldColors,
                isError = showError,
                visualTransformation = PhoneNumberTransformation(isPickCountry.countryAlphaCode),
                placeholder = {
                    phonePlaceholder("")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    autoCorrect = true,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    },
                ),
                leadingIcon = {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                            .clickableNoRipple {
                                expanded = !expanded
                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            painter = painterResource(
                                id = getCountryFlags(
                                    isPickCountry.countryAlphaCode.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )

                        Icon(
                            modifier = Modifier.rotate(rotationState),
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF8C8C8C)
                        )

                        if (showCountryCode) {
                            Text(
                                text = isPickCountry.countryPhoneCode,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(start = 4.dp),
                                fontSize = 18.sp,
                                color = Color(0xFF9B9B9B),
                            )
                        }
                    }
                },
                trailingIcon = if (shouldShowTrailingIcon()) {
                    {
                        if (showError) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (shouldShowClearTrailingIcon()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickableNoRipple {
                                        onValueChange.invoke("")
                                    }
                            )
                        } else customRightIcon?.invoke()
                    }
                } else null
            )

            if (showError && errorText.isNullOrEmpty().not()) {
                Text(
                    modifier = Modifier
                        .padding(errorTextPaddings),
                    text = errorText.orEmpty(),
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SearchCountry(
                    searchFieldColors = searchFieldColors,
                    searchPlaceholder = searchPlaceholder,
                    onSelected = {
                        isPickCountry = it
                        phoneCode = it.countryPhoneCode
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchCountry(
    searchFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    searchPlaceholder: @Composable ((txtPlaceHolder: String?) -> Unit) = { txtPlaceHolder ->
        Text(
            text = txtPlaceHolder.orEmpty(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraLight
            ),
        )
    },
    onSelected: (item: Country) -> Unit
) {
    var filteredCountries: List<Country> = emptyList()
    var searchValue by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(235.dp)
            .border(width = 1.dp, color = Color(0xFFDADADA), shape = RoundedCornerShape(8.dp))
    ) {
        stickyHeader {
            SearchField(
                searchFieldColors = searchFieldColors,
                searchPlaceholder = searchPlaceholder,
                filteredCountries = {
                    filteredCountries = it
                },
                searchValue = searchValue,
                onChangeSearchValue = {
                    searchValue = it
                }
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color(0xFFDADADA)
            )
        }

        val countries = if (searchValue.isEmpty()) countryList else filteredCountries

        items(countries) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickableNoRipple {
                        onSelected.invoke(it)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    painter = painterResource(
                        id = getCountryFlags(
                            it.countryAlphaCode.lowercase(
                                Locale.getDefault()
                            )
                        )
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = mergeNameAndCode(it.name, it.countryPhoneCode),
                    style = TextStyle.Default,
                    color = Color(0xFF212121),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SearchField(
    searchFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    searchValue: String,
    onChangeSearchValue: (String) -> Unit,
    searchPlaceholder: @Composable ((txtPlaceHolder: String?) -> Unit) = { txtPlaceHolder ->
        Text(
            text = txtPlaceHolder.orEmpty(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    },
    filteredCountries: (MutableList<Country>) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .focusRequester(focusRequester),
        value = searchValue,
        onValueChange = { searchStr ->
            onChangeSearchValue.invoke(searchStr)
            filteredCountries.invoke(countryList.filter {
                it.name.contains(
                    searchStr,
                    ignoreCase = true,
                ) ||
                        it.countryPhoneCode.contains(
                            searchStr,
                            ignoreCase = true,
                        ) ||
                        it.countryAlphaCode.contains(
                            searchStr,
                            ignoreCase = true,
                        )
            }.toMutableList())
        },
        placeholder = {
            searchPlaceholder.invoke("Search...")
        },
        colors = searchFieldColors,
        textStyle = MaterialTheme.typography.labelLarge,
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    )
}