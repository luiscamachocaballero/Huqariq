package com.itsigned.huqariq.helper

class ValidationHelper {

    companion object {

        /**
         * Metodo para validar un correo
         * @param email correo a validar
         * @return boleano indicando si sepaso la validación o no
         */
        fun validateMail(email: String): Boolean {
            val regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$"
            return email.matches(regex.toRegex())
        }

        /**
         * Metodo para validar el formato de un DNI
         * @param identifyNumber dni a validar
         */
        fun validateIdentifyNumber(identifyNumber:String):Boolean{
            if (identifyNumber.isEmpty())return false
            if(identifyNumber.length!=8)return false
            return true
        }



    }
}