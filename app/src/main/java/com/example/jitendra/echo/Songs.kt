package com.example.jitendra.echo

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Jitendra on 07-01-2018.
 */
class Songs(var songId: Long, var songTitle: String,var artist:String,var songData:String,var dateadded:Long) : Parcelable{
    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    override fun describeContents(): Int {
        return 0

    }
    object Statified{
        var nameComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)

        }
        var dateComprator: Comparator<Songs> = Comparator<Songs>{ song1,song2 ->
            val songOne = song1.dateadded.toDouble()
            val songTwo = song2.dateadded.toDouble()
            songTwo.compareTo(songOne)





        }
    }


}