package com.itsigned.huqariq.model

data class LoginRequestDto(
        var email:String,
        var password:String
)

data class LoginUserDto(
        var count:Any ,
        var first_name: String,
        var last_name: String,
        var user_app_id: Int,
        var dni: Int,
        var email: String,
        var lang:Int
)

data class RegisterUserDto(
        var email: String,
        var password:String,
        var phone:String,
        var distrito_id:String,
        var region_id:String,
        var last_name:String,
        var first_name:String,
        var provincia_id:String,
        var dni:String,
       var native_lang:Int
)

data class RequestValidateMail(
        var email:String
)

data class RequestValidateDni(
        var dni:String
)

data class  ResponseLangDto(
        val language:List<Language>
)

data class  ResponseValidateDni(
        val name:String,
        val first_name: String,
        val last_name: String
)

data class Language(
        val iso:String,
        val language_id:Int,
        val name:String
){
    override fun toString(): String {
        return name
    }
}


data class FormRegisterUserStepOneDto(
        var email:String,
        var password:String,
        var dni:String,
        var name:String,
        var surname:String
)

data class FormRegisterUserStepTwoDto(
        var regionId :String,
        var provinciaId :String,
        var distritoId :String,
        var phone:String,
        var nativeLang:Int
)


data class FormRegisterStepThreeDto(
       var idDialect:String
)

data class FormDialectRegion(
        var departamento:String,
        var provincia:String,
        var distrito:String
)

data class FormDialectAnswer(
        var respuesta:Array<String>

)


data class ResponseDialectRegion(
        var dialecto:String
)

data class ResponseDialectAnswer(
        var dialecto:String
)