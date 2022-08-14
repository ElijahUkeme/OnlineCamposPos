package com.elijah.ukeme.firebase.signup.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener {
            logInUser()
        }
        textview_login.setOnClickListener {
            finish()
        }
    }
    private fun logInUser(){
        val email = email.text.toString()
        val password = password.text.toString()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {

            }
    }
}