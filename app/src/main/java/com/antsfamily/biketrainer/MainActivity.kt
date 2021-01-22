package com.antsfamily.biketrainer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antsfamily.biketrainer.MainApplication.Companion.PROGRAM_IMAGES_PATH
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PROGRAM_IMAGES_PATH = "${applicationInfo.dataDir}/programs"
    }
}
