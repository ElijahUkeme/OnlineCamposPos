package com.elijah.ukeme.firebase.signup.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener {
            createUser()
        }

        textview_login.setOnClickListener {
            val intoLogin = Intent(this, LoginActivity::class.java)
            startActivity(intoLogin)
        }
        button_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }
         var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==0 && resultCode== Activity.RESULT_OK && data !=null) {
            // proceed and check what the selected photo was............
              selectedPhotoUri =data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            select_photo_imageview.setImageBitmap(bitmap)
            button_photo.alpha = 0f
           // val bitmapDrawable = BitmapDrawable(bitmap)
           // button_photo.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun createUser() {

        val email = email.text.toString()
        val password = password.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields can't be empty, please fill it", Toast.LENGTH_SHORT).show()
        } else
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                        return@addOnCompleteListener
                    //else create user with email and password
                   Toast.makeText(this,"Successfully created user with uid: ${it.result!!.user!!.uid}"
                   ,Toast.LENGTH_SHORT).show()
                    uploadPhotoToFirebaseStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Fail to create user ${it.message}", Toast.LENGTH_SHORT).show()
                }

    }
    private fun uploadPhotoToFirebaseStorage(){
        if(selectedPhotoUri ==null)
            return
        val filename =UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(this,"Successfully uploaded image to firebase ${it.metadata?.path}",Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    safeUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }
    private fun safeUserToFirebaseDatabase(profileImageUri: String){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user= User(uid,username.text.toString(),profileImageUri)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this,"Finally we saved the user to firebase database",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

}
@Parcelize
class User(val uid: String, val username: String, val profileImageUri: String): Parcelable{
    constructor(): this("","","")
}
