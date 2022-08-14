package com.elijah.ukeme.firebase.signup.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerView_chat.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user!!.profileImageUri
       // setUpChatMessage()
        listenToMessages()

        button_send_chat.setOnClickListener {
            performSendMessage()
        }
    }
    private fun listenToMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage !=null){
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    }else
                    adapter.add(ChatToItem(chatMessage.text))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    class ChatMessage(val id:String, val text:String, val fromId: String, val toId:String, val timestamp:Long){
        constructor() : this("","","","",-1)
    }
    private fun performSendMessage(){
        val text = editText_chat.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid

        if(fromId ==null)return
        val reference = FirebaseDatabase.getInstance().getReference("/message").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
            reference.setValue(chatMessage)
    }
    private fun setUpChatMessage(){
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("From Message"))
        adapter.add(ChatToItem("To Message\nTo Message"))

        recyclerView_chat.adapter = adapter
    }
}
class ChatFromItem(val text: String): Item<ViewHolder>(){
    override fun getLayout(): Int {
     return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_message_from_row.text = text

    }

}
class ChatToItem(val text: String): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_message_to_row.text = text

    }

}