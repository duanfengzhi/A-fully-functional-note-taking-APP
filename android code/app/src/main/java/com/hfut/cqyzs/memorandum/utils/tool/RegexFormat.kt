package com.hfut.cqyzs.memorandum.utils.common

import java.util.regex.Pattern

class RegexFormat {

    fun regexPhone(phone: String): Boolean {
        val phoneRegex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,3,5-9])|(177))\\d{8}$"
        val p = Pattern.compile(phoneRegex)
        val m = p.matcher(phone)
        return m.matches()
    }

    fun regexPassword(phone: String): Boolean {
        val passwordRegex = "^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$"
        val p = Pattern.compile(passwordRegex)
        val m = p.matcher(phone)
        return m.matches()
    }

    fun regexEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?"
        val p = Pattern.compile(emailRegex)
        val m = p.matcher(email)
        return m.matches()
    }
}