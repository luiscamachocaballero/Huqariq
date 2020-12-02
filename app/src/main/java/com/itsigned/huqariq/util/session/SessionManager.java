package com.itsigned.huqariq.util.session;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.itsigned.huqariq.activity.LoginActivity;
import com.itsigned.huqariq.bean.User;
import com.itsigned.huqariq.util.Constants;


/**
 * Clase encargada del manejo de la sesión de usuario
 */
public class SessionManager {

    private static final String TAG = "SessionManager";
    private static SessionManager _sessionManager;
    private Context _applicationContext;
    private SharedPreferences _pref;

    private SessionManager(Context applicationContext) {
        _applicationContext = applicationContext;
        _pref = applicationContext.getSharedPreferences(Constants.USER_PRIVATE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Metodo para obtner un singleton de la clase
     * @param applicationContext contexto actual
     * @return singleton de la clase
     */
    public static SessionManager getInstance(Context applicationContext) {
        if (_sessionManager == null) {
            _sessionManager = new SessionManager(applicationContext);
        }
        return (_sessionManager);
    }

    /**
     * Metodo para crear una sesión de usuario en el sharedPreferences
     * @param user usuario del cual se creará la sesion
     */
    public void createUserSession(User user) {
        SharedPreferences.Editor prefsEditor = _pref.edit();
        Gson gson = new Gson();
        String s_userr = gson.toJson(user);
        prefsEditor.putString(Constants.USER_SESSION_KEY, s_userr);
        prefsEditor.apply();
    }

    /**
     * Metodo para eliminar la sesión del sharedPreferences
     */
    public void removeUserSession() {
        SharedPreferences.Editor prefsEditor = _pref.edit();
        prefsEditor.clear();
        prefsEditor.apply();

    }

    /**
     * Metodo que retorna el usuario logueado
     * @return usuario logueado
     */
    public User getUserLogged() {
        String s_user = _pref.getString(Constants.USER_SESSION_KEY, null);
        return (s_user == null) ? null : new Gson().fromJson(s_user, User.class);
    }


    /**
     * Metodo para validar si un usuario esta logueado
     * @return boleano que indica si un usuario esta logueado
     */
    public boolean isLogged() {
        return getUserLogged() != null;

    }

    /**
     * Metodo para cerrar la sesion del usuario actual
     */
    public void logoutApp() {
        removeUserSession();
        Intent i=new Intent((_applicationContext), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _applicationContext.startActivity(i);
    }

}
