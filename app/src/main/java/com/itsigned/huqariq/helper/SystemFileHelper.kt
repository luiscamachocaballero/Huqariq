package com.itsigned.huqariq.helper

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.ResponseBody
import java.io.*
import java.util.*


class SystemFileHelper {

    companion object {

        /**
         * Metodo para obtener la ruta de un archivo
         * @param nameDirectory nombre del directorio donde se debe buscar
         * @param nameFile nombre del archivo a busrcar
         */
        fun getPathFile(nameDirectory: String, nameFile: String): String {
            return "${Environment.getExternalStorageDirectory().absolutePath}/$nameDirectory/$nameFile"
        }


        /**
         * Metodo para escribir una respuesta de web en un archivo
         * @param context contexto a usar
         * @param body responseBody con información stream a guardar
         * @param nameDirectory directorio donde se guardara el achivo a escribir
         * @param nameFile nombre del archivo a escribir
         */
        fun writeResponseBodyToDisk(context: Context, body: ResponseBody, nameDirectory:String, nameFile:String): Boolean {

            Log.d("SystemFileHelper","write in "+nameDirectory+" file:"+nameFile)
            createDirectory(nameDirectory)
            try {
                val futureStudioIconFile = File(getPathFile(nameDirectory,nameFile))
                if(!futureStudioIconFile.exists())futureStudioIconFile.createNewFile()
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    var fileSizeDownloaded: Long = 0
                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(futureStudioIconFile)

                    while (true) {
                        val read = inputStream.read(fileReader)
                        if (read == -1)break
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()
                    }
                    outputStream.flush()
                    return true


                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                } finally {
                    if (inputStream != null) inputStream.close()
                    if (outputStream != null) outputStream.close()

                }
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * Metodo utiliatario  para crear un directorio
         * @param nameDirectory nombre del directorio a crear
         */
        fun createDirectory(nameDirectory: String): File {
            val folder = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + nameDirectory)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return folder
        }


        /**
         * Metodo utiliatario  para generar nombres de archivos unicos
         * @param prefix prefijo del nombre del archivo
         * @param extension extension del archivo
         */
        fun getNameCodeFile(prefix: String, extension: String): String {
            return "$prefix${Date().time}.$extension"
        }

    }

}