package com.itsigned.huqariq.mapper

import com.itsigned.huqariq.bean.User
import com.itsigned.huqariq.model.LoginUserDto
import com.itsigned.huqariq.model.RegisterUserDto

class GeneralMapper {
    companion object{

        fun userToRegisterUserDto(toMap: User): RegisterUserDto {
            return RegisterUserDto(
                    email = toMap.email,
                    password = toMap.password,
                    dni = toMap.dni,
                    first_name = toMap.firstName,
                    last_name = toMap.lastName,
                    provincia_id = toMap.codeProvincia.toString(),
                    distrito_id = toMap.codeDistrito.toString(),
                    region_id = toMap.codeDepartamento.toString(),
                    phone = toMap.phone.toString(),
                    native_lang=toMap.idLanguage
            )
        }

        fun loginUserDtoDtoToUser(toMap: LoginUserDto): User {
            val user= User()
            user.email = toMap.email
            user.userExternId = toMap.user_app_id.toLong()
            user.dni = toMap.dni.toString()
            user.firstName = toMap.first_name
            user.lastName = toMap.last_name
          //  user.idLanguage=toMap.lang
            user.avance = if  (toMap.count==null||toMap.count.toString()=="")0 else  toMap.count.toString().replace("[", "").replace("]", "").toFloat().toInt()
            user.codeDepartamento = 1
            user.codeDistrito = 1
            user.codeProvincia = 1
            return user
        }
    }
}