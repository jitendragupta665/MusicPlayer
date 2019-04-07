package com.example.jitendra.echo.fragment


import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.jitendra.echo.R
import com.example.jitendra.echo.Songs
import com.example.jitendra.echo.activities.MainActivity
import com.example.jitendra.echo.adopters.FavoriteAdopter
import com.example.jitendra.echo.database.EchoDatabase


/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {
    var myActivity: Activity? = null

    var noFavorites: TextView? = null
    var nowPlayingBottomBar: ImageButton? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView?= null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null

    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null
    object Satified{
        var mediaPlayer: MediaPlayer? = null
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        activity.title = "Favorites"
        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
       display_favorites_by_searching()
        bottomBarSetup()



    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }
    fun getsongfromphone():ArrayList<Songs>{
        var arraylist = ArrayList<Songs>()
        var contetResover = myActivity?.contentResolver
        var songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songcursor = contetResover?.query(songuri,null,null,null,null)
        if (songcursor != null && songcursor.moveToFirst()){
            val songId = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songtitle =  songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songartist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songdata = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateindex = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songcursor.moveToNext()){
                var currentid =  songcursor.getLong(songId)
                var currenttitle =  songcursor.getString(songtitle)
                var currentartist =  songcursor.getString(songartist)
                var currentdata =  songcursor.getString(songdata)
                var currentdate =  songcursor.getLong(dateindex)
                arraylist.add(Songs(currentid,currenttitle,currentartist,currentdata,currentdate))


            }

        }
        return arraylist
    }
    fun bottomBarSetup(){
        try {
            bottomBArClickHandler()
            songTitle?.setText(SongPlayingFragment.Statisfied.currentsonghelper?.songTitle)
            SongPlayingFragment.Statisfied.mediaPlayer?.setOnCompletionListener({

                songTitle?.setText(SongPlayingFragment.Statisfied.currentsonghelper?.songTitle)
                SongPlayingFragment.Statiscated.onSongComplete()

            })
            if (SongPlayingFragment.Statisfied.mediaPlayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility = View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE


            }
        }catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
    fun bottomBArClickHandler(){
            nowPlayingBottomBar?.setOnClickListener({
                Satified.mediaPlayer = SongPlayingFragment.Statisfied.mediaPlayer

            val songPlayingfragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist",SongPlayingFragment.Statisfied.currentsonghelper?.songArtist)
            args.putString("path",SongPlayingFragment.Statisfied.currentsonghelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.Statisfied.currentsonghelper?.songTitle)
            args.putInt("songid",SongPlayingFragment.Statisfied.currentsonghelper?.songid?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Statisfied.currentsonghelper?.currrentposition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.Statisfied.fetchsongs)
            args.putString("FavBottomBar","success")
            songPlayingfragment.arguments = args
            fragmentManager.beginTransaction()
                    .replace(R.id.detailsfragment,songPlayingfragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        })
       playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statisfied.mediaPlayer?.isPlaying as Boolean){
                SongPlayingFragment.Statisfied.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statisfied.mediaPlayer?.getCurrentPosition() as Int
               playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.Statisfied.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statisfied.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)


            }


        })
    }
    fun display_favorites_by_searching(){
        if (favoriteContent?.checkSize() as Int>0){
            refreshList = ArrayList<Songs>()
            getListfromDatabase = favoriteContent?.queryDBList()
            var fetchListFromDevice = getsongfromphone()
            if (fetchListFromDevice != null){
                for (i in 0..fetchListFromDevice?.size - 1){
                    for (j in 0..getListfromDatabase?.size as Int - 1){
                        if ((getListfromDatabase?.get(j)?.songId) == (fetchListFromDevice?.get(i)?.songId)){
                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                        }

                    }
                }
            }else{

            }
            if (refreshList == null)
            {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE

            }else{
                var favoriteadopter = FavoriteAdopter(refreshList as ArrayList<Songs>,myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteadopter
                recyclerView?.setHasFixedSize(true)
            }

        }else{
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE

        }



    }

}// Required empty public constructor
