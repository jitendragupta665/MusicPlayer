package com.example.jitendra.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.jitendra.echo.R
import com.example.jitendra.echo.activities.MainActivity
import com.example.jitendra.echo.fragment.SongPlayingFragment

/**
 * Created by Jitendra on 24-01-2018.
 */
class CaptureBroadcast: BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statified.notificationManager?.cancel(1998)
            }catch(e : Exception){
                e.printStackTrace()
            }
           try {
               if (SongPlayingFragment.Statisfied.mediaPlayer?.isPlaying as Boolean){
                   SongPlayingFragment.Statisfied.mediaPlayer?.pause()
                   SongPlayingFragment.Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.play_icon)

               }
           }catch (e : Exception){
               e.printStackTrace()
           }
        }else{
            val tm: TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1998)
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statisfied.mediaPlayer?.isPlaying as Boolean){
                            SongPlayingFragment.Statisfied.mediaPlayer?.pause()
                            SongPlayingFragment.Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.play_icon)

                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }
                else ->{

                }
            }
        }
    }

}