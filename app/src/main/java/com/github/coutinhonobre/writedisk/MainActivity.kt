package com.github.coutinhonobre.writedisk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

class MainActivity : AppCompatActivity() {

    private val writer = Writer()
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            count++
            writer.write("Log de teste no onClickListener $count")
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            exportLogFile()
        }
    }

    fun exportLogFile() {
        val logFile = writer.getFile()
        logFile?.let {
            val fileUri = FileProvider.getUriForFile(
                this,
                "com.github.coutinhonobre.writedisk.provider",
                it
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    inner class Writer {
        fun write(message: String) {
            val logFile = getFile()
            PrintWriter(FileOutputStream(logFile, true)).use { writer ->
                writer.println("${System.currentTimeMillis()} - $message")
            }
        }

        fun getFile(): File {
            val logDir = getExternalFilesDir(null)?.absolutePath + "/records"
            val logFile = File(logDir, "app.txt")
            if (!logFile.exists()) {
                logFile.parentFile?.mkdirs()
                logFile.createNewFile()
            }
            return logFile
        }
    }
}

