package com.loftymr.countrycodepicker.utils

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun isPhoneNumberValid(phoneNumberStr: String): Boolean {
    return try {
        val phoneNumber =
            PhoneNumberUtil.getInstance().parse(
                phoneNumberStr.trim(),
                null
            )
        PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)
    } catch (e: NumberParseException) {
        e.printStackTrace()
        false
    }
}