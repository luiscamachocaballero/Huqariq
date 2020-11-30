package com.itsigned.huqariq.helper

import android.content.Context
import android.os.Environment
import okhttp3.ResponseBody
import java.io.*
import java.util.*


class SystemFileHelper {

    companion object {

        fun getPathFile(nameDirectory: String, nameFile: String): String {
            return "${Environment.getExternalStorageDirectory().absolutePath}/$nameDirectory/$nameFile"
        }

        fun writeResponseBodyToDisk(context: Context, body: ResponseBody, nameDirectory:String, nameFile:String): Boolean {
            createDirectory(nameDirectory)
            try {
                val futureStudioIconFile = File(getPathFile(nameDirectory,nameFile))
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
                    return false
                } finally {
                    if (inputStream != null) inputStream.close()
                    if (outputStream != null) outputStream.close()

                }
            } catch (e: IOException) {
                return false
            }

        }

        fun createDirectory(nameDirectory: String): File {
            val folder = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + nameDirectory)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return folder
        }
        fun getNameCodeFile(prefix: String, extension: String): String {
            return "$prefix${Date().time}.$extension"
        }

    }

}