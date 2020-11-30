package com.itsigned.huqariq.util.session;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.itsigned.huqariq.activity.LoginActivity;
import com.itsigned.huqariq.bean.User;
import com.itsigned.huqariq.util.Constants;



public class SessionManager {

    private static final String TAG = "SessionManager";
    private static SessionManager _sessionManager;
    private Context _applicationContext;
    private SharedPreferences _pref;

    private SessionManager(Context applicationContext) {
        _applicationContext = applicationContext;
        _pref = applicationContext.getSharedPreferences(Constants.USER_PRIVATE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance(Context applicationContext) {
        if (_sessionManager == null) {
            _sessionManager = new SessionManager(applicationContext);
        }
        return (_sessionManager);
    }

    public void createUserSession(User user) {

        SharedPreferences.Editor prefsEditor = _pref.edit();
        Gson gson = new Gson();
        String s_userr = gson.toJson(user);
        prefsEditor.putString(Constants.USER_SESSION_KEY, s_userr);
        prefsEditor.apply();
    }

    public void removeUserSession() {
        //eliminar session de usuario creada.
        SharedPreferences.Editor prefsEditor = _pref.edit();
        prefsEditor.clear();
        prefsEditor.apply();

    }


    public User getUserLogged() {
        String s_user = _pref.getString(Constants.USER_SESSION_KEY, null);
        return (s_user == null) ? null : new Gson().fromJson(s_user, User.class);
    }


    public boolean isLogged() {
        return getUserLogged() != null;

    }

    public void logoutApp() {
        removeUserSession();

        Intent i=new Intent((_applicationContext), LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _applicationContext.startActivity(i);
    }

}
