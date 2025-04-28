package com.yorguisanchez.unabfit_r.ui.theme

import android.util.Patterns

fun validationEmail (email: String):Pair<Boolean,String>{
    return  when{
        email.isEmpty() -> Pair(false, "El correo es requerido")
        !email.endsWith("@unab.edu.co") -> Pair(false, "Solo correos institucionales (@unab.edu.co)")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Pair(false, "Solo correos validos")

        else ->{
            Pair(true, "")
        }
    }

}

fun validatepassword (password:String): Pair<Boolean,String>{

    return when{
        password.isEmpty() -> Pair(false, "La contaseña es requerida")
        password.length < 8 -> Pair(false, "La contraseña debe tener minimo 8 caracteres")

        else -> {
            Pair(true, "")
        }
    }
}


fun validateEmailRegister(email: String): Pair<Boolean, String> {
    return when {
        email.isEmpty() -> Pair(false, "El correo es requerido")
        !email.endsWith("@unab.edu.co") -> Pair(false, "Solo correos institucionales @unab.edu.co")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Pair(false, "Correo no válido")
        else -> Pair(true, "")
    }
}

fun validatePasswordRegister(password: String): Pair<Boolean, String> {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }

    return when {
        password.isEmpty() -> Pair(false, "La contraseña es requerida")
        password.length < 8 -> Pair(false, "La contraseña debe tener mínimo 8 caracteres")
        !hasUpperCase -> Pair(false, "La contraseña debe tener al menos una letra mayúscula")
        !hasDigit -> Pair(false, "La contraseña debe tener al menos un número")
        else -> Pair(true, "")
    }
}