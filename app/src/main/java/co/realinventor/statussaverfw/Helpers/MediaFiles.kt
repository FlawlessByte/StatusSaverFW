package co.realinventor.statussaverfw.Helpers

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*


object MediaFiles {
    private var allFiles = ArrayList<String>()
    private var imageFiles = ArrayList<String>()
    private var videoFiles = ArrayList<String>()
    private var savedVideoFiles = ArrayList<String>()
    private var savedImageFiles = ArrayList<String>()
    private var savedFiles = ArrayList<String>()
    private val APP_FOLDER_NAME = "StatusSaver"
    var WHATSAPP_STATUS_FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + "/Whatsapp/Media/.Statuses/"
    val DOWNLOADED_IMAGE_PATH = Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME + "/Saved/"


    //Copy to App folder operations
    fun copyToDownload(path: String) {
        val src = File(path)
        val des = File(DOWNLOADED_IMAGE_PATH, src.name)
        try {
            copyFile(src, des)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File) {
        if (!destFile.parentFile.exists())
            destFile.parentFile.mkdirs()

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination!!.transferFrom(source, 0, source!!.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }


    fun initSavedFiles() {
        val directory = File(DOWNLOADED_IMAGE_PATH)
        savedFiles.clear()
        savedVideoFiles.clear()
        savedImageFiles.clear()

        val files = directory.listFiles()
        //        Log.d("Files", "Size: "+ files.length);
        for (i in files!!.indices) {
            if (files[i].name.endsWith(".mp4") || files[i].name.endsWith(".jpg")) {
                savedFiles.add(files[i].name)
            }
            if (files[i].name.endsWith(".mp4")) {
                savedVideoFiles.add(files[i].name)
            }
            if (files[i].name.endsWith(".jpg")) {
                savedImageFiles.add(files[i].name)
            }
        }

    }

    fun getSavedFiles(): ArrayList<String> {
        return savedFiles
    }

    fun getSavedVideoFiles(): ArrayList<String> {
        return savedVideoFiles
    }

    fun getSavedImageFiles(): ArrayList<String> {
        return savedImageFiles
    }

    fun doesWhatsappDirExist(): Boolean {
        val dir = File(WHATSAPP_STATUS_FOLDER_PATH)
        return if (dir.exists() && dir.isDirectory) {
            true
        } else {
            false
        }
    }

    fun initAppDirectrories() {
        var dir = File(Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME)
        if (dir.exists() && dir.isDirectory) {
            dir = File(Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME + "/Saved")
            if (!(dir.exists() && dir.isDirectory)) {
                dir.mkdir()
            }
        } else {
            dir.mkdir()
            dir = File(Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME + "/Saved")
            dir.mkdir()
        }
    }

    //Method to initialise allFiles, imageFiles, videoFiles
    fun initMediaFiles() {
        imageFiles.clear()
        videoFiles.clear()
        allFiles.clear()

        val directory = File(WHATSAPP_STATUS_FOLDER_PATH)
        //        Log.d("Directory exists:",""+doesWhatsappDirExist());
        val files = directory.listFiles()

        if (files != null) {   //Handling crash NullPointerException
            //        Log.d("Files", "Size: "+ files.length);
            for (i in files.indices) {
                allFiles.add(files[i].name)
                if (files[i].name.endsWith(".jpg") || files[i].name.endsWith(".gif")) {
                    imageFiles.add(files[i].name)
                } else if (files[i].name.endsWith(".mp4")) {
                    videoFiles.add(files[i].name)
                }
            }
        }
    }

    fun getAllFiles(): ArrayList<String> {
        return allFiles
    }

    fun getImageFiles(): ArrayList<String> {
        return imageFiles
    }

    fun getVideoFiles(): ArrayList<String> {
        return videoFiles
    }


    fun removeExpired(): Float {

        val directory = File(WHATSAPP_STATUS_FOLDER_PATH)
        //        Log.d("Directory exists:",""+doesWhatsappDirExist());
        val files = directory.listFiles()
        val currentTime = Date()

        var file_size_total = 0f

        val cal = Calendar.getInstance()
        cal.time = currentTime
        cal.add(Calendar.DATE, -1)
        val dateBeforeOneDay = cal.time
        Log.d("Limit date", dateBeforeOneDay.toString())

        if (files != null) {
            var file_count = 0

            for (i in files.indices) {
                if (Date(files[i].lastModified()).before(dateBeforeOneDay)) {
                    file_count++
                    file_size_total += files[i].length().toFloat()
                    files[i].delete()
                }
            }
            Log.d("File count ", "Expired files $file_count")
            Log.d("File size ", "Total size of files freed $file_size_total")

            file_size_total = file_size_total / 1024 / 1024
        }

        return file_size_total
    }
}
