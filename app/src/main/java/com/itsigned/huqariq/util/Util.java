package com.itsigned.huqariq.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import com.itsigned.huqariq.R;
import com.itsigned.huqariq.helper.SystemFileHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


/**
 * Clase con metodos utilitarios
 */
public class Util {


    /**
     * Metodo para validar un correo
     * @param email correo a validar
     * @return boleano indicando si se paso la validacion o no
     */
    public static boolean validarCorreo(String email) {

        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();

    }




    public static String getSimpleDateToday() {
        DateTime today = new DateTime(DateTimeZone.forID(Constants.DATE_TIME_ZONE_AMERICA_LATINA));
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.FORMAT_DATE, Locale.getDefault());
        return sdf.format(today.toDate());
    }


    /**
     * Metodo para copiar archivos
     * @param source archivo de origen
     * @param context contexto a trabajar
     * @throws IOException
     */
    public static void copyFileUsingStream(File source,Context context) throws IOException {
        InputStream is = null;
        FileOutputStream fos = context.openFileOutput(source.getName(), Context.MODE_PRIVATE);
        try {
            is = new FileInputStream(source);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } finally {
            is.close();
            fos.close();
        }
    }

    /**
     * Metodo para obtner un progresDialog personalizad
     * @param mContext contexto a trabajar
     * @param mensaje mensaje a colocar en el progress dialog
     * @return progressdialog obtenido
     */
    public static ProgressDialog createProgressDialog(Context mContext,String mensaje) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        if (mensaje!=null) dialog.setMessage(mensaje);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        return dialog;
    }

    /**
     * Metodo para obtener un string en base a un formato de fecha
     * @return cadena obtenida
     */
    public static String getStringDate(){
        String date = Util.getSimpleDateToday();
        String[] parts = date.split("-");
        if (parts[1].equals("01")){
            return  parts[2]+"ene".concat(parts[0]);
        }else if (parts[1].equals("02")){
            return parts[2]+"feb".concat(parts[0]);
        }else if (parts[1].equals("03")){
            return  parts[2]+"mar".concat(parts[0]);
        }else if (parts[1].equals("04")){
            return  parts[2]+"abr".concat(parts[0]);
        }else if (parts[1].equals("05")){
            return  parts[2]+"may".concat(parts[0]);
        }else if (parts[1].equals("06")){
            return  parts[2]+"jun".concat(parts[0]);
        }else if (parts[1].equals("07")){
            return  parts[2]+"jul".concat(parts[0]);
        }else if (parts[1].equals("08")){
            return  parts[2]+"ago".concat(parts[0]);
        }else if (parts[1].equals("09")){
            return  parts[2]+"set".concat(parts[0]);
        }else if (parts[1].equals("10")){
            return  parts[2]+"oct".concat(parts[0]);
        }else if (parts[1].equals("11")){
            return  parts[2]+"nov".concat(parts[0]);
        }else{
            return  parts[2]+"dec".concat(parts[0]);

        }

    }


    public static String saveConsentiment(Context context) {
        InputStream in = null;
        FileOutputStream fout = null;
        String pathToDownload="";
        File sd = null;
        try {
            in = context.getResources().openRawResource(R.raw.consentimiento);
            String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();
            String filename = "consentimiento.pdf";
            pathToDownload=downloadsDirectoryPath + "/"+ filename;

            sd=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator +filename);

            fout = new FileOutputStream(sd);
            Log.d("File","downloaded "+pathToDownload);
            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sd.getAbsolutePath();
    }

}
