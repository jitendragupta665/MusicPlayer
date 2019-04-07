package com.example.jitendra.echo.fragment


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.jitendra.echo.CurrentSongHelper
import com.example.jitendra.echo.R
import com.example.jitendra.echo.Songs
import com.example.jitendra.echo.database.EchoDatabase
import com.example.jitendra.echo.fragment.SongPlayingFragment.Statisfied.glview
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {
    object  Statisfied{
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseimagebutton: ImageButton? = null
        var nextimagebtn : ImageButton? = null
        var loopimagebtn : ImageButton? = null
        var previousimagebtn : ImageButton? = null
        var seekbar : SeekBar? = null
        var songartistview: TextView? = null
        var songtitleview: TextView? = null
        var shuffleimagebtn : ImageButton? = null
        var currentsonghelper : CurrentSongHelper? = null
        var currentpostion: Int = 0
        var fab: ImageButton? = null
        var audiovisualization: AudioVisualization? = null
        var glview: GLAudioVisualizationView? = null
        var fetchsongs:ArrayList<Songs>? = null
        var favoriteContent:EchoDatabase? = null
        var mSensorManager: SensorManager? = null
        var mSensorListner: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"


        var updateSongTime = object : Runnable{
            override fun run() {
                val getcurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d: %d",TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long)) ))
                seekbar?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this,1000)
            }


        }

    }




    object Statiscated{
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"
        fun onSongComplete(){
            if (Statisfied.currentsonghelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
                Statisfied.currentsonghelper?.isPlaying = true
            }else{
                if (Statisfied.currentsonghelper?.isloop as Boolean){
                    Statisfied.currentsonghelper?.isPlaying = true
                    var nextsong = Statisfied.fetchsongs?.get(Statisfied.currentpostion)
                    Statisfied.currentsonghelper?.songPath = nextsong?.songData
                    Statisfied.currentsonghelper?.songTitle = nextsong?.songTitle
                    Statisfied.currentsonghelper?.songid = nextsong?.songId as Long
                    Statisfied.currentsonghelper?.currrentposition = Statisfied.currentpostion
                    Statiscated.updateTExtView(Statisfied.currentsonghelper?.songTitle as String,Statisfied.currentsonghelper?.songArtist as String)
                    Statisfied.mediaPlayer?.reset()
                    try {
                        Statisfied.mediaPlayer?.setDataSource(Statisfied.myActivity, Uri.parse(Statisfied.currentsonghelper?.songPath))
                        Statisfied.mediaPlayer?.prepare()
                        Statisfied.mediaPlayer?.start()
                        Statiscated.processInformation(Statisfied.mediaPlayer as MediaPlayer)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }


                }else{
                    playNext("PlayNextNormal")
                    Statisfied.currentsonghelper?.isPlaying = true
                }
            }
            if (Statisfied.favoriteContent?.checkidIdExists(Statisfied.currentsonghelper?.songid?.toInt() as Int) as Boolean){

                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_on))
            }else{
                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_off))

            }
        }

        fun playNext(check: String){
            if (check.equals("PlayNextNormal",true)){
                Statisfied.currentpostion =  Statisfied.currentpostion + 1
            }else if(check.equals("PlayNextLikeNormalShuffle",true)){
                var randomObject = Random()
                var randomposition = randomObject.nextInt(Statisfied.fetchsongs?.size?.plus(1) as Int)
                Statisfied.currentpostion = randomposition



            }
            if (Statisfied.currentpostion == Statisfied.fetchsongs?.size){
                Statisfied.currentpostion = 0
            }
            Statisfied.currentsonghelper?.isloop = false
            var nextsong = Statisfied.fetchsongs?.get(Statisfied.currentpostion)
            Statisfied.currentsonghelper?.songPath = nextsong?.songData
            Statisfied.currentsonghelper?.songTitle = nextsong?.songTitle
            Statisfied.currentsonghelper?.songid = nextsong?.songId as Long
            Statisfied.currentsonghelper?.currrentposition = Statisfied.currentpostion
            Statiscated.updateTExtView(Statisfied.currentsonghelper?.songTitle as String,Statisfied.currentsonghelper?.songArtist as String)
            Statisfied.mediaPlayer?.reset()
            try {
                Statisfied.mediaPlayer?.setDataSource(Statisfied.myActivity, Uri.parse(Statisfied.currentsonghelper?.songPath))
                Statisfied.mediaPlayer?.prepare()
                Statisfied.mediaPlayer?.start()
                Statiscated.processInformation(Statisfied.mediaPlayer as MediaPlayer)
            }catch (e : Exception){
                e.printStackTrace()
            }
            if (Statisfied.favoriteContent?.checkidIdExists(Statisfied.currentsonghelper?.songid?.toInt() as Int) as Boolean){

                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_on))
            }else{
                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_off))

            }

        }
        fun processInformation(mediaPlayer: MediaPlayer){
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statisfied.seekbar?.max = finalTime
            Statisfied.startTimeText?.setText(String.format("%d: %d",TimeUnit.MILLISECONDS.toMinutes( startTime.toLong()),TimeUnit.MILLISECONDS.toSeconds( startTime.toLong() ) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes( startTime.toLong())) ))
            Statisfied.endTimeText?.setText(String.format("%d: %d",TimeUnit.MILLISECONDS.toMinutes( finalTime.toLong()),TimeUnit.MILLISECONDS.toSeconds( finalTime.toLong() ) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes( finalTime.toLong())) ))
            Statisfied.seekbar?.setProgress(startTime)
            Handler().postDelayed(Statisfied.updateSongTime,1000)
        }

        fun updateTExtView(songtitle:String,songartist: String){
            var songTitleUpdated = songtitle
            var songArtistUpdated = songartist
            if (songtitle.equals("<unknown>",true)){
                songTitleUpdated = "unknown"
            }
            if (songartist.equals("<unknown>",true)){
                songArtistUpdated = "unknown"
            }
            Statisfied.songtitleview?.setText(songTitleUpdated)
            Statisfied.songartistview?.setText(songArtistUpdated)
        }


    }
    var mAccelaration: Float = 0f
    var mAccelarationCurrent: Float = 0f
    var mAccelarationLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view =  inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity.title = "Now Playing"
        Statisfied.seekbar = view?.findViewById(R.id.seekBar)
        Statisfied.startTimeText = view?.findViewById(R.id.starttime)
        Statisfied.endTimeText = view?.findViewById(R.id.endtime)
        Statisfied.playpauseimagebutton = view?.findViewById(R.id.playpausebutton)
        Statisfied.nextimagebtn = view?.findViewById(R.id.nextbutton)
        Statisfied.previousimagebtn = view?.findViewById(R.id.previousbutton)
        Statisfied.loopimagebtn = view?.findViewById(R.id.loopbutton)
        Statisfied.shuffleimagebtn = view?.findViewById(R.id.shufflebutton)
        Statisfied.songartistview = view?.findViewById(R.id.songartist)
        Statisfied.songtitleview = view?.findViewById(R.id.songTitle)
        Statisfied.glview = view?.findViewById(R.id.visualizer_view)
        Statisfied.fab = view?.findViewById(R.id.favoriteIcon)
        Statisfied.fab?.alpha = 0.8f

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statisfied.audiovisualization = glview as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statisfied.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statisfied.myActivity =  activity
    }

    override fun onResume() {
        super.onResume()
        Statisfied.audiovisualization?.onResume()
        Statisfied.mSensorManager?.registerListener(Statisfied.mSensorListner,
                Statisfied.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Statisfied.audiovisualization?.onPause()
        Statisfied.mSensorManager?.unregisterListener(Statisfied.mSensorListner)
    }

    override fun onDestroyView() {
        Statisfied.audiovisualization?.release()
        super.onDestroyView()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statisfied.mSensorManager =Statisfied.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
         mAccelaration = 0.0f
        mAccelarationCurrent = SensorManager.GRAVITY_EARTH
        mAccelarationLast = SensorManager.GRAVITY_EARTH
       bindShakeListner()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item:MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2:MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect->{
                Statisfied.myActivity?.onBackPressed()
                return false
            }


        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statisfied.favoriteContent = EchoDatabase(Statisfied.myActivity)
        Statisfied.currentsonghelper = CurrentSongHelper()
        Statisfied.currentsonghelper?.isPlaying = true
        Statisfied.currentsonghelper?.isloop = false
        Statisfied.currentsonghelper?.isShuffle = false
        var path:String? = null
        var _songtitle:String? = null
        var _songartist:String? = null
        var songid:Long =0
        try {
            path = arguments.getString("path")
            _songtitle = arguments.getString("songTitle")
            _songartist = arguments.getString("songArtist")
            songid = arguments.getInt("songid").toLong()
            Statisfied.currentpostion = arguments.getInt("songPostion")
            Statisfied.fetchsongs = arguments.getParcelableArrayList("songData")
            Statisfied.currentsonghelper?.songPath = path
            Statisfied.currentsonghelper?.songArtist = _songartist
            Statisfied.currentsonghelper?.songTitle = _songtitle
            Statisfied.currentsonghelper?.songid = songid
            Statisfied.currentsonghelper?.currrentposition =  Statisfied.currentpostion
            Statiscated.updateTExtView( Statisfied.currentsonghelper?.songTitle as String, Statisfied.currentsonghelper?.songArtist as String)
        }catch (e : Exception)
        {
            e.printStackTrace()
        }
        var fromFavBottomBAr = arguments.get("FavBottomBar") as? String
        if (fromFavBottomBAr != null){
            Statisfied.mediaPlayer = FavoriteFragment.Satified.mediaPlayer
        }else {
            Statisfied.mediaPlayer = MediaPlayer()
            Statisfied.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statisfied.mediaPlayer?.setDataSource(Statisfied.myActivity, Uri.parse(path))
                Statisfied.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statisfied.mediaPlayer?.start()
        }
        Statiscated.processInformation( Statisfied.mediaPlayer as MediaPlayer)
        if ( Statisfied.currentsonghelper?.isPlaying as Boolean){
            Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.pause_icon)
        }else{
            Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statisfied.mediaPlayer?.setOnCompletionListener {
            Statiscated.onSongComplete()

        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler( Statisfied.myActivity as Context,0)
        Statisfied.audiovisualization?.linkTo(visualizationHandler)
        var prefsforshuffle  = Statisfied.myActivity?.getSharedPreferences(Statiscated?.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleallowed = prefsforshuffle?.getBoolean("feature",false)
        if (isShuffleallowed as Boolean){
            Statisfied.currentsonghelper?.isShuffle = true
            Statisfied.currentsonghelper?.isloop = false
            Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_icon)
            Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_white_icon)
        }else{
            Statisfied.currentsonghelper?.isShuffle = false
            Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }
        var prefsforLoop  = Statisfied.myActivity?.getSharedPreferences(Statiscated?.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopallowed = prefsforshuffle?.getBoolean("feature",false)
        if (isLoopallowed as Boolean){
            Statisfied.currentsonghelper?.isShuffle = false
            Statisfied.currentsonghelper?.isloop = true
            Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_icon)
        }else{
            Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_white_icon)
            Statisfied.currentsonghelper?.isloop = false

        }
        if ( Statisfied.favoriteContent?.checkidIdExists( Statisfied.currentsonghelper?.songid?.toInt() as Int) as Boolean){

            Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable( Statisfied.myActivity,R.drawable.favorite_off))
            Statisfied.favoriteContent?.deleteFavorite( Statisfied.currentsonghelper?.songid?.toInt() as Int)
            Toast.makeText(Statisfied.myActivity,"Removed from favorites",Toast.LENGTH_SHORT).show()
        }else{
            Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_on))
            Statisfied.favoriteContent?.storeAsFavorite(Statisfied.currentsonghelper?.songid?.toInt(),Statisfied.currentsonghelper?.songArtist,
                    Statisfied.currentsonghelper?.songTitle,Statisfied.currentsonghelper?.songPath)
            Toast.makeText(Statisfied.myActivity,"Added to favorites",Toast.LENGTH_SHORT).show()


        }


    }
    fun clickHandler(){
        Statisfied.fab?.setOnClickListener({
            if (Statisfied.favoriteContent?.checkidIdExists(Statisfied.currentsonghelper?.songid?.toInt() as Int) as Boolean){
                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_on))

            }else{
                Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity,R.drawable.favorite_off))

            }



        })

        Statisfied.shuffleimagebtn?.setOnClickListener({
            var editorShuffle = Statisfied.myActivity?.getSharedPreferences(Statiscated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statisfied.myActivity?.getSharedPreferences(Statiscated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
            if (Statisfied.currentsonghelper?.isShuffle as Boolean){
                Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statisfied.currentsonghelper?.isShuffle = false
            editorShuffle?.putBoolean("feature",false)
            editorShuffle?.apply()}
            else{
                Statisfied.currentsonghelper?.isShuffle = true
                Statisfied.currentsonghelper?.isloop = false
                Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_icon)
                Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }

        })
        Statisfied.nextimagebtn?.setOnClickListener({
            Statisfied.currentsonghelper?.isPlaying = true
            Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statisfied.currentsonghelper?.isShuffle as Boolean){
                Statiscated.playNext("PlayNextLikeNormalShuffle")

            }else{
                Statiscated.playNext("PlayNextNormal")
            }


        })
        Statisfied.previousimagebtn?.setOnClickListener({
            Statisfied.currentsonghelper?.isPlaying = true
            if (Statisfied.currentsonghelper?.isloop as Boolean){
                Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_white_icon)


            }
            playprevious()


        })
        Statisfied.loopimagebtn?.setOnClickListener({
            var editorShuffle = Statisfied.myActivity?.getSharedPreferences(Statiscated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statisfied.myActivity?.getSharedPreferences(Statiscated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()

            if (Statisfied.currentsonghelper?.isloop as Boolean){
                Statisfied.currentsonghelper?.isloop = false
                Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()

            }
            else{
                Statisfied.currentsonghelper?.isloop = true
                Statisfied.currentsonghelper?.isShuffle = false
                Statisfied.loopimagebtn?.setBackgroundResource(R.drawable.loop_icon)
                Statisfied.shuffleimagebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
            }





        })
        Statisfied.playpauseimagebutton?.setOnClickListener({
            if (Statisfied.mediaPlayer?.isPlaying as Boolean){
                Statisfied.mediaPlayer?.pause()
                Statisfied.currentsonghelper?.isPlaying = false
                Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                Statisfied.mediaPlayer?.start()
                Statisfied.currentsonghelper?.isPlaying = true
                Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.pause_icon)


            }


        })
    }

    fun playprevious() {
        Statisfied.currentpostion = Statisfied.currentpostion - 1
        if (Statisfied.currentpostion == -1) {
            Statisfied.currentpostion = 0
        }
        if (Statisfied.currentsonghelper?.isPlaying as Boolean) {
            Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statisfied.playpauseimagebutton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statisfied.currentsonghelper?.isloop = false
        var nextsong = Statisfied.fetchsongs?.get(Statisfied.currentpostion)
        Statisfied.currentsonghelper?.songPath = nextsong?.songData
        Statisfied.currentsonghelper?.songTitle = nextsong?.songTitle
        Statisfied.currentsonghelper?.songid = nextsong?.songId as Long
        Statisfied.currentsonghelper?.currrentposition = Statisfied.currentpostion
        Statiscated.updateTExtView(Statisfied.currentsonghelper?.songTitle as String, Statisfied.currentsonghelper?.songArtist as String)
        Statisfied.mediaPlayer?.reset()
        try {
            Statisfied.mediaPlayer?.setDataSource(Statisfied.myActivity, Uri.parse(Statisfied.currentsonghelper?.songPath))
            Statisfied.mediaPlayer?.prepare()
            Statisfied.mediaPlayer?.start()
            Statiscated.processInformation(Statisfied.mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Statisfied.favoriteContent?.checkidIdExists(Statisfied.currentsonghelper?.songid?.toInt() as Int) as Boolean) {

            Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity, R.drawable.favorite_on))
        } else {
            Statisfied.fab?.setImageDrawable(ContextCompat.getDrawable(Statisfied.myActivity, R.drawable.favorite_off))

        }
    }
        fun bindShakeListner(){
            Statisfied.mSensorListner = object : SensorEventListener{
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                }

                override fun onSensorChanged(p0: SensorEvent) {
                    val x = p0.values[0]
                    val y = p0.values[1]
                    val z = p0.values[2]

                    mAccelarationLast = mAccelarationCurrent
                    mAccelarationCurrent = Math.sqrt(((x*x + y*y + z*z).toDouble())).toFloat()
                    val delta = mAccelarationCurrent - mAccelarationLast
                    mAccelaration = mAccelaration * 0.9f + delta

                    if (mAccelaration > 12){
                        val prefs =  Statisfied.myActivity?.getSharedPreferences(Statisfied.MY_PREFS_NAME,Context.MODE_PRIVATE)
                        val isAllowed = prefs?.getBoolean("feature",false)
                        if (isAllowed as Boolean){
                            Statiscated.playNext("playNextNormal")

                                             }


                    }


                }


            }


        }




}//  Required empty public constructor
