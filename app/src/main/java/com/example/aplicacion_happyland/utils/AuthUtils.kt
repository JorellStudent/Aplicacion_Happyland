package com.example.aplicacion_happyland.utils

import android.content.Context
import android.content.SharedPreferences

// Nombre del archivo de SharedPreferences
private const val PREFS_NAME = "HappylandPrefs"
// Clave para guardar el estado de inicio de sesión
private const val KEY_IS_LOGGED_IN = "isLoggedIn"

/**
 * Guarda el estado de inicio de sesión en SharedPreferences.
 *
 * @param context El contexto de la aplicación.
 * @param isLoggedIn Booleano que indica si el usuario está logueado.
 */
fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    editor.apply()
}

/**
 * Verifica si el usuario está logueado.
 *
 * @param context El contexto de la aplicación.
 * @return Booleano que indica si el usuario está logueado.
 */
fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
}

/**
 * Cierra la sesión del usuario eliminando el estado de inicio de sesión.
 *
 * @param context El contexto de la aplicación.
 */
fun logout(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear() // Elimina todas las claves almacenadas
    editor.apply()
}
