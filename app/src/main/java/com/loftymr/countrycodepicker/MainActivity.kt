package com.loftymr.countrycodepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.loftymr.countrycodepicker.ui.theme.CountryCodePickerTheme
import com.loftymr.countrycp.CountryCP
import com.loftymr.countrycp.utils.EMPTY_STRING
import com.loftymr.countrycp.utils.isPhoneNumberValid

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountryCodePickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    color = Color.White
                ) {
                    Column {
                        val phoneNumber = remember { mutableStateOf(EMPTY_STRING) }
                        val fullPhoneNumber = remember { mutableStateOf(EMPTY_STRING) }
                        val showError = remember { mutableStateOf(false) }

                        CountryCP(
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = phoneNumber.value,
                            shape = RoundedCornerShape(8.dp),
                            showError = showError.value,
                            errorText = "Enter correct phone number",
                            onValueChange = { phoneNumber.value = it },
                            phonePlaceholder = {
                                Text(text = "xxx xxx xx xx")
                            },
                            errorTextPaddings = PaddingValues(vertical = 2.dp, horizontal = 16.dp),
                            onFullNumberValue = { fullPhoneNumber.value = it },
                            phoneFieldColors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.LightGray,
                                errorContainerColor = if (showError.value) Color(0xFFFFE5E5) else Color.Transparent
                            ),
                            searchFieldColors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = Color(0xFFE5E5E5),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFE5E5E5),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color(0xFFDADADA)
                            )
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            onClick = {
                                showError.value = !isPhoneNumberValid(fullPhoneNumber.value)
                            }
                        ) {
                            Text(text = "Click Me!")
                        }
                    }
                }
            }
        }
    }
}