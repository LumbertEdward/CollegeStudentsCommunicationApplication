package com.example.tuchatapplication.chatclasses

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tuchatapplication.R
import com.example.tuchatapplication.adapters.ChatAdapter
import com.example.tuchatapplication.interfaces.Generalinterface
import com.example.tuchatapplication.models.Chat
import com.example.tuchatapplication.viewmodels.ChattingViewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ChatRoom : Fragment(), View.OnClickListener {
    private val TAG = "ChatRoom"
    private lateinit var chat: EditText
    private lateinit var send: ImageView
    private lateinit var attach: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var desc: TextView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var back: RelativeLayout
    private lateinit var title: TextView
    private lateinit var chattingViewModel: ChattingViewModel
    private var groupId: String? = null
    private var userId: String? = null
    private var chatList: ArrayList<Chat> = ArrayList()
    private lateinit var generalinterface: Generalinterface
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chattingViewModel = ViewModelProvider(requireActivity()).get(ChattingViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerChats)
        chat = view.findViewById(R.id.editChat)
        attach = view.findViewById(R.id.attach)
        send = view.findViewById(R.id.send)
        back = view.findViewById(R.id.relBackChats)
        title = view.findViewById(R.id.txtTitleChat)
        desc = view.findViewById(R.id.txtDesc)

        linearLayoutManager = LinearLayoutManager(activity)
        chatAdapter = ChatAdapter(activity as Context)

        attach.setOnClickListener(this)
        send.setOnClickListener(this)
        back.setOnClickListener(this)

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = linearLayoutManager

        getGroupDetails()

        getChats()

        getUserId()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    var chatPic = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null){
            filePath = it.data!!.data!!
            try {
                val source = ImageDecoder.createSource(requireActivity().contentResolver,
                    filePath!!
                )
                val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)

            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    })

    private fun getUserId() {
        var sharedPrefs: SharedPreferences = activity?.getSharedPreferences(getString(R.string.User),
            Context.MODE_PRIVATE
        )!!

        userId = sharedPrefs.getString(getString(R.string.id), "")
    }

    private fun getGroupDetails() {
        var sharedPrefs: SharedPreferences = activity?.getSharedPreferences("GROUPID",
            Context.MODE_PRIVATE
        )!!

        groupId = sharedPrefs.getString("groupId", "")

        chattingViewModel.getGroupDetails(groupId!!).observe(viewLifecycleOwner, Observer {
            if (it != null){
                title.text = it.group_name
                desc.text = it.group_description
            }
        })
    }

    private fun getChats() {
        chattingViewModel.getGroupChats(groupId!!).observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                setChats(it)
            }
        })
    }

    private fun setChats(it: List<Chat>?) {
        for (i in it!!){
            chatList.add(i)
        }

        chatAdapter.getData(chatList)
        recyclerView.scrollToPosition(chatList.size - 1)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.attach -> {
                val intent: Intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                chatPic.launch(intent)
            }
            R.id.send -> {
                sendChat()
            }
            R.id.relBackChats -> {
                generalinterface.goToMainPage()
            }
        }

    }

    private fun sendChat() {
        Log.i(TAG, "sendChat: Sending")
        var date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        var chatId: String = Random.nextInt(10, 1000).toString()
        var time = SimpleDateFormat("hh:mm").format(Date())
        var message = chat.text.toString().trim()

        if (!TextUtils.isEmpty(message)){
            var nChat: Chat = Chat()
            nChat.userId = userId
            nChat.groupId = groupId
            nChat.time = time
            nChat.date = date
            nChat.message = message
            nChat.chatId = chatId

            var response = chattingViewModel.addChat(nChat)
            if (response >= 0){
                Log.i(TAG, "sendChat: Added")
                chatList.add(nChat)
                if (chatList.size > 0){
                    chatAdapter.addNewData(nChat, chatList.size - 1)
                    recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
            else{
                Log.i(TAG, "sendChat: Not Added")
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        generalinterface = context as Generalinterface
    }
}