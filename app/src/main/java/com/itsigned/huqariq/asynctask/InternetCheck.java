package com.itsigned.huqariq.asynctask;

import android.os.AsyncTask;

import com.itsigned.huqariq.util.Util;



public class InternetCheck extends AsyncTask<Void,Void,Boolean> {

    private Consumer mConsumer;
    public  interface Consumer { void accept(Boolean internet); }

    public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }

    @Override protected Boolean doInBackground(Void... voids) {

        return Util.isOnline();


    }

    @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
}
///////////////////////////////////////////////////////////////////////////////////
// Usage

