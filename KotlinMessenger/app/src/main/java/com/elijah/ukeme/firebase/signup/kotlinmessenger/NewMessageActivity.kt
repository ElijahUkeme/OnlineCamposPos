package com.elijah.ukeme.firebase.signup.kotlinmessenger

import android.accounts.AccountManager.get
import android.content.ClipData
import android.content.Intent
import android.media.CamcorderProfile.get
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.get
import androidx.appcompat.widget.AppCompatDrawableManager.get
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.reflect.TypeToken.get
import com.squareup.okhttp.HttpUrl.get
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.lang.reflect.Array.get

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

       /* val adapter =GroupAdapter<com.xwray.groupie.ViewHolder>()
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        recycler_message.adapter*/

        fetchUser()

    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<com.xwray.groupie.ViewHolder>()
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user !=null){
                        adapter.add(UserItem(user))

                        adapter.setOnItemClickListener { item, view ->
                            val userItem = item as UserItem
                            val intent = Intent(view.context,ChatLogActivity::class.java)
                            //intent.putExtra(USER_KEY,userItem.user.username)
                            intent.putExtra(USER_KEY,userItem.user)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                recycler_message.adapter = adapter
            }

        })
    }
}
class UserItem(val user:User): Item<com.xwray.groupie.ViewHolder>(){
    override fun bind(viewHolder: com.xwray.groupie.ViewHolder, position: Int) {
        viewHolder.itemView.textview_username_message.text = user.username

        Picasso.get().load(user.profileImageUri).into(viewHolder.itemView.imageview_message)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}


