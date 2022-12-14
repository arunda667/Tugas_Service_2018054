package com.example.mservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.collections.ArrayList


class MyService : Service() {

    val TAG = "TAG"
    private val CHANNEL_ID = "chanel_id_exam_01"
    private var mediaPlayer: MediaPlayer ?= null
    private var trackList : ArrayList<TrackFiles> ?= null
    var positNow = 0
    var countCallCommand  = 0

    private val ACTION_PAUSE = 1
    private val ACTION_RESUME = 2
    private val ACTION_CLEAR = 3
    private val ACTION_PRE = 4
    private val ACTION_NEXT = 5


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //create channel for notification
        creatChannelNoti()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        @Suppress("UNCHECKED_CAST")
        val _trackList = intent?.getSerializableExtra("tracklist") as? ArrayList<TrackFiles>

        if(_trackList!=null) {
            trackList = _trackList
        }
        if(countCallCommand==0) {
            startMusic(trackList!![positNow].media)
        }
        countCallCommand ++


        //create notification
        sendNoti(trackList!!, positNow)

        val actionMusic = intent!!.getIntExtra("action_mucsic_backser",0)
        handleActionMusic(actionMusic)

        return START_STICKY
    }


    override fun onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun startMusic(media: Int) {
        mediaPlayer = MediaPlayer.create(this, media)
        mediaPlayer?.isLooping=true
        mediaPlayer?.start()
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_RESUME -> resumeMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_CLEAR -> clearMusic()
            ACTION_PRE -> preMusic()
            ACTION_NEXT -> nextMusic()
            else -> return
        }
    }

    private fun nextMusic() {
        if(positNow == trackList!!.size - 1 ) {
            pauseMusic()
        } else {
            positNow++
            mediaPlayer!!.release()
            startMusic(trackList!![positNow].media)
            sendNoti(trackList!!,positNow)
        }
    }

    private fun preMusic() {
        if(positNow == 0 ) {
            pauseMusic()
        } else {
            positNow--
            mediaPlayer!!.release()
            startMusic(trackList!![positNow].media)
            sendNoti(trackList!!,positNow)
        }
    }

    private fun clearMusic() {
        stopSelf()
    }

    private fun pauseMusic() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
            sendNoti(trackList!!,positNow)
        }
    }

    private fun resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
            sendNoti(trackList!!,positNow)
        }
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("action_music", action)
        return PendingIntent.getBroadcast(this, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun creatChannelNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Noti Title"
            val descrip = "Noti Descrip"
            val impor = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, impor).apply {
                description = descrip
            }
            channel.setSound(null, null)
            val notiMana: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notiMana.createNotificationChannel(channel)
        }
    }

    private fun sendNoti(trackList: ArrayList<TrackFiles>, positNow: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val remoteview = RemoteViews(packageName, R.layout.layout_custom_notification)
        remoteview.setTextViewText(R.id.tv_title, trackList[positNow].title)
        remoteview.setTextViewText(R.id.tv_single, trackList[positNow].singer)
        remoteview.setImageViewBitmap(
            R.id.img_song,
            BitmapFactory.decodeResource(
                applicationContext.resources,
                trackList[positNow].thumbnail
            )
        )
        remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.pause)
        if (mediaPlayer!!.isPlaying) {
            remoteview.setOnClickPendingIntent(
                R.id.img_play_pause,
                getPendingIntent(this, ACTION_PAUSE)
            )
            remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.pause)

        } else {
            remoteview.setOnClickPendingIntent(
                R.id.img_play_pause,
                getPendingIntent(this, ACTION_RESUME)
            )
            remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.conti)
        }
        remoteview.setOnClickPendingIntent(
            R.id.img_clear,
            getPendingIntent(this, ACTION_CLEAR)
        )
        remoteview.setOnClickPendingIntent(
            R.id.img_pre,
            getPendingIntent(this, ACTION_PRE)
        )
        remoteview.setOnClickPendingIntent(
            R.id.img_next,
            getPendingIntent(this, ACTION_NEXT)
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteview)
            .setSound(null)
            .build()
        startForeground(1, builder)
    }



}