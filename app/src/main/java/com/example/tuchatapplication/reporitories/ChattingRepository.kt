package com.example.tuchatapplication.reporitories

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.tuchatapplication.models.Chat
import com.example.tuchatapplication.models.Group
import com.example.tuchatapplication.models.Member
import com.example.tuchatapplication.sqlitedatabase.helpers.ChatsSqliteDatabaseHelper
import com.example.tuchatapplication.sqlitedatabase.helpers.GroupsSqliteDatabaseHelper
import com.example.tuchatapplication.sqlitedatabase.helpers.MembersSqliteDatabaseHelper
import com.example.tuchatapplication.sqlitedatabase.tables.ChatsTable
import com.example.tuchatapplication.sqlitedatabase.tables.GroupTable
import com.example.tuchatapplication.sqlitedatabase.tables.MembersTable

class ChattingRepository(context: Context) {
    private val TAG = "Dashboard"
    private var chatsSqliteDatabaseHelper: ChatsSqliteDatabaseHelper? = null

    init {
        chatsSqliteDatabaseHelper = ChatsSqliteDatabaseHelper(context)
    }

    companion object{
        private var INSTANCE: ChattingRepository? = null

        fun setInstance(context: Context): ChattingRepository{
            if (INSTANCE == null){
                INSTANCE = ChattingRepository(context)
            }

            return INSTANCE!!
        }
    }

    //CHATTING
    suspend fun addChat(chat: Chat): Long{
        if (chat != null){
            var db = chatsSqliteDatabaseHelper!!.writableDatabase
            var contentValues: ContentValues = ContentValues().apply {
                put(ChatsTable.COLUMN_NAME_CHAT_ID, chat.chatId)
                put(ChatsTable.COLUMN_NAME_USERID, chat.userId)
                put(ChatsTable.COLUMN_NAME_GROUPID, chat.groupId)
                put(ChatsTable.COLUMN_NAME_MESSAGE, chat.message)
                put(ChatsTable.COLUMN_NAME_DATE, chat.date)
                put(ChatsTable.COLUMN_NAME_TIME, chat.time)
            }

            var response = db.insert(ChatsTable.TABLE_NAME, null, contentValues)
            if (response >= 0){
                return response
            }
            else{
                return -1
            }
        }
        else{
            return -1
        }
    }

    suspend fun getChats(groupId: String): ArrayList<Chat>{
        var db = chatsSqliteDatabaseHelper!!.readableDatabase
        var projection = arrayOf(ChatsTable.COLUMN_NAME_CHAT_ID, ChatsTable.COLUMN_NAME_USERID, ChatsTable.COLUMN_NAME_TIME,
        ChatsTable.COLUMN_NAME_MESSAGE, ChatsTable.COLUMN_NAME_GROUPID, ChatsTable.COLUMN_NAME_DATE, ChatsTable.COLUMN_NAME_ID)
        var selection = "${ChatsTable.COLUMN_NAME_GROUPID} = ?"
        var selectionArgs = arrayOf(groupId)

        var cursor = db.query(ChatsTable.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        null)

        var lst: ArrayList<Chat> = ArrayList()

        while (cursor.moveToNext()){
            var chat: Chat = Chat()
            chat.chatId = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_CHAT_ID))
            chat.date = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_DATE))
            chat.groupId = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_GROUPID))
            chat.message = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_MESSAGE))
            chat.time = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_TIME))
            chat.userId = cursor.getString(cursor.getColumnIndexOrThrow(ChatsTable.COLUMN_NAME_USERID))

            lst.add(chat)
        }

        return lst
    }

}