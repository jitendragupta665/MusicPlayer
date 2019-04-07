package com.example.jitendra.echo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.jitendra.echo.Songs
import com.example.jitendra.echo.database.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.example.jitendra.echo.database.EchoDatabase.Staticated.COLUMN_SONG_PATH
import com.example.jitendra.echo.database.EchoDatabase.Staticated.COLUMN_SONG_TITLE

/**
 * Created by Jitendra on 15-01-2018.
 */
class EchoDatabase : SQLiteOpenHelper{
    var  songList = ArrayList<Songs>()


    object Staticated{

        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
        var DB_VERSION = 1
        val DB_NAME = "FavoriteDataBase"

    }
    override fun onCreate(sqlitedatabase: SQLiteDatabase?) {
            sqlitedatabase?.execSQL( "CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID + " INTEGER," + COLUMN_SONG_ARTIST + " STRING,"
                    + COLUMN_SONG_TITLE + " STRING," + COLUMN_SONG_PATH + " STRING);")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storeAsFavorite(id: Int?, artist: String?,title: String?,path: String?){
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(Staticated.COLUMN_ID,id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST,artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE,title)
        contentValues.put(Staticated.COLUMN_SONG_PATH,path)
        db.insert(Staticated.TABLE_NAME,null,contentValues)
        db.close()
    }
    fun queryDBList() : ArrayList<Songs>?{
        try {
           val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params,null)
            if (cSor.moveToFirst()){
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _tite = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songpath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    songList.add(Songs(_id.toLong(),_tite,_artist,_songpath,0))
                }while(cSor.moveToNext())
            }else {
                return null
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return songList

    }
    fun checkidIdExists(_id: Int) : Boolean{
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(query_params,null)
        if (cSor.moveToFirst()){
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))


            }while (cSor.moveToNext())


        }else{
            return false
        }
        return storeId != -1090
    }
    fun deleteFavorite(_id:Int){
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID + "=" + _id,null)
        db.close()
    }
    fun checkSize(): Int{
        var counter = 0
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params,null)
        if (cSor.moveToFirst()) {
            do {
                counter = counter + 1


            } while (cSor.moveToNext())


        }else {
            return 0
        }
        return counter

    }
}