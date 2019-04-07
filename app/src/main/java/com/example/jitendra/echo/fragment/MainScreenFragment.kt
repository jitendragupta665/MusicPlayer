package com.example.jitendra.echo.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.jitendra.echo.R
import com.example.jitendra.echo.Songs
import com.example.jitendra.echo.adopters.MainScreenAdopter
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {
    var getsonglist: ArrayList<Songs>? = null
    var nowplayingbottombar:RelativeLayout? = null
    var playpausebutton: ImageButton? = null
    var visiblelaayout: RelativeLayout? = null
    var songtitle: TextView? = null
    var nosongs:RelativeLayout? = null
    var recylerview: RecyclerView? = null
    var myactivity: Activity? = null
    var _mainscreenadopter: MainScreenAdopter? =null



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        activity.title = "All songs"
        val view =  inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        visiblelaayout = view?.findViewById<RelativeLayout>(R.id.visibleyout)
        nosongs = view?.findViewById<RelativeLayout>(R.id.nosongs)
        nowplayingbottombar= view?.findViewById<RelativeLayout>(R.id.hiddenbarmainscreen)
        songtitle = view?.findViewById<TextView>(R.id.songtitlemainscreen)
        playpausebutton = view?.findViewById<ImageButton>(R.id.playpausebutton)
        recylerview = view?.findViewById<RecyclerView>(R.id.contentMain)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getsonglist = getsongfromphone()
        val prefs = activity.getSharedPreferences("action sort",Context.MODE_PRIVATE)
        val action_sort_ascending = prefs.getString("action_sort_ascending","true")
        val action_sort_recent = prefs.getString("action_sort_recent","false")
        if (getsonglist == null){
            visiblelaayout?.visibility = View.INVISIBLE
            nosongs?.visibility = View.VISIBLE
        }else{
            _mainscreenadopter = MainScreenAdopter(getsonglist as ArrayList<Songs>, myactivity as Context)
            val mLayoutManager = LinearLayoutManager(myactivity)
            recylerview?.layoutManager = mLayoutManager
            recylerview?.itemAnimator = DefaultItemAnimator()
            recylerview?.adapter = _mainscreenadopter
        }



        if (getsonglist != null){
            if (action_sort_ascending!!.equals("true",true)){
                Collections.sort(getsonglist,Songs.Statified.nameComparator)
                _mainscreenadopter?.notifyDataSetChanged()

            }else if (action_sort_recent!!.equals("true",true)){
                Collections.sort(getsonglist,Songs.Statified.dateComprator)
                _mainscreenadopter?.notifyDataSetChanged()
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return



    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending){
            val editor = myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending","true")
            editor?.putString("action_sort_recent","false")
            editor?.apply()
            if (getsonglist != null){
                Collections.sort(getsonglist,Songs.Statified.nameComparator)

        }
            _mainscreenadopter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent){
            val editortwo = myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent","true")
            editortwo?.putString("action_sort_ascending","false")
            editortwo?.apply()
            if (getsonglist != null){
                Collections.sort(getsonglist,Songs.Statified.dateComprator)

            }
            _mainscreenadopter?.notifyDataSetChanged()
            return false

            }
        return super.onOptionsItemSelected(item)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }
    fun getsongfromphone():ArrayList<Songs>{
        var arraylist = ArrayList<Songs>()
        var contetResover = myactivity?.contentResolver
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

}// Required empty public constructor
