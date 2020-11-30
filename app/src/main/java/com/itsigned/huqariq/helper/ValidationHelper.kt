package com.itsigned.huqariq.helper

class ValidationHelper {

    companion object {
        fun validateCellPhone(cellPhone:String):Boolean{
            if (cellPhone.matches(Regex("^[0-9]*$")) && cellPhone.length==9 ) return true
            return false
        }

        fun validateMail(email: String): Boolean {
            val regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$"
            return email.matches(regex.toRegex())
        }

        fun validateIdentifyNumber(identifyNumber:String):Boolean{
            if (identifyNumber.isNullOrEmpty())return false
            if(identifyNumber.length!=8)return false
            return true
        }

        fun validateStringEmpty(value:String):Boolean{
            return !value.isNullOrEmpty()
        }



    }
}