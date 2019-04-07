package com.example.jitendra.echo.adopters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.jitendra.echo.R
import com.example.jitendra.echo.activities.MainActivity
import com.example.jitendra.echo.fragment.AboutUsFragment
import com.example.jitendra.echo.fragment.FavoriteFragment
import com.example.jitendra.echo.fragment.MainScreenFragment
import com.example.jitendra.echo.fragment.SettingsFragment

/**
 * Created by Jitendra on 05-01-2018.
 */
class NavigationDrawerAdopter(_contentList: ArrayList<String>,getImages:IntArray,_context: Context)
    : RecyclerView.Adapter<NavigationDrawerAdopter.NavViewHolder>(){
    var contentList: ArrayList<String>? = null
    var getimages: IntArray? = null
    var mcontext: Context? = null
    init {
        this.contentList = _contentList
        this.getimages = getImages
        this.mcontext = _context
    }


    override fun onBindViewHolder(holder: NavViewHolder?, position: Int) {
         holder?.icon_Get?.setBackgroundResource(getimages?.get(position) as Int)
        holder?.text_Get?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({
            if (position == 0){
                val mainScreenfragment = MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailsfragment,mainScreenfragment)
                        .commit()
            }
            else if(position == 1)
            { val favoritefragment = FavoriteFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailsfragment,favoritefragment)
                        .commit()

            }else if (position == 2){
                val settingfragment = SettingsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailsfragment,settingfragment)
                        .commit()
            }
            else{
                val aboutusfragment = AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailsfragment,aboutusfragment)
                        .commit()
            }

            MainActivity.Statified.drawerLayout?.closeDrawers()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavViewHolder {
        var itemview = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigation_bar,parent,false)
        val returnthis = NavViewHolder(itemview)
        return returnthis
    }
    override fun getItemCount(): Int {
        return contentList?.size as Int

    }


    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var icon_Get: ImageView? = null
        var text_Get: TextView? = null
        var contentHolder: RelativeLayout? = null
        init {
            icon_Get = itemView?.findViewById(R.id.icon_navdrawer)
            text_Get = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }

    }
}