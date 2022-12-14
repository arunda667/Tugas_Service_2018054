package com.example.mservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mIntent = Intent(this,MyService::class.java)
        poppulateFiles()
        mIntent.putExtra("tracklist",trackFilesArrayList)
        btn_start.setOnClickListener(){
            startService(mIntent)
        }
        btn_stop.setOnClickListener(){
            stopService(mIntent)
        }

    }

    private fun poppulateFiles() {
        trackFilesArrayList.add(
            TrackFiles("Test","AnhTu",
                R.drawable.one,
                R.raw.nhac)
        )
        trackFilesArrayList.add(
            TrackFiles("Faded","Alan",
                R.drawable.two,
                R.raw.faded)
        )
        trackFilesArrayList.add(
            TrackFiles("Private","Guitar",
                R.drawable.three,
                R.raw.guitar)
        )
    }
    var trackFilesArrayList = ArrayList<TrackFiles>()


    //Bà B sửa
    private fun test(){
        val A = B;
        val B = A;
    }
    

}