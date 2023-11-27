package com.loftymr.countrycp.utils

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.loftymr.countrycp.Country
import kotlinx.coroutines.CoroutineScope

const val EMPTY_STRING = ""

fun Modifier.clickableNoRipple(
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed {
    clickable(
        indication = null,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}

fun mergeNameAndCode(countryName: String, countryCode: String): String {
    return "$countryName ($countryCode)"
}

@Composable
fun CCPLaunchedEffect(
    key1: Any? = Unit,
    block: suspend CoroutineScope.(isInit: Boolean) -> Unit
) {
    var isInit by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(key1) {
        block.invoke(this, isInit)
        isInit = false
    }
}


fun List<Country>.filterCountries(context: Context, searchStr: String): MutableList<Country> {
    return filter { country ->
        context.getString(country.countryName).contains(searchStr, ignoreCase = true) || country.countryPhoneCode.contains(searchStr, ignoreCase = true) || country.countryAlphaCode.contains(searchStr, ignoreCase = true)
    }.toMutableList()
}