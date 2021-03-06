package com.example.jitendra.echo.adopters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.jitendra.echo.R
import com.example.jitendra.echo.Songs
import com.example.jitendra.echo.fragment.SongPlayingFragment

/**
 * Created by Jitendra on 07-01-2018.
 */
class MainScreenAdopter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<MainScreenAdopter.MyViewHolder>() {
    var songDetails: ArrayList<Songs>? = null
    var mcontext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mcontext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.tracktitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist
        holder.contentHolder?.setOnClickListener({
            try {
                if (SongPlayingFragment.Statisfied.mediaPlayer?.isPlaying as Boolean){
                    SongPlayingFragment.Statisfied.mediaPlayer?.stop()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
            val songPlayingfragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist",songObject?.artist)
            args.putString("path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putInt("songid",songObject?.songId?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)
            songPlayingfragment.arguments = args
            (mcontext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailsfragment,songPlayingfragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()

        })

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val itemview = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tracktitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackArtist = view.findViewById(R.id.rackartist)
            tracktitle = view.findViewById(R.id.tracktitle)
            contentHolder = view.findViewById(R.id.contentrow)
        }
    }
}