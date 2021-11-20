package com.example.tuchatapplication.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tuchatapplication.MainActivity
import com.example.tuchatapplication.R
import com.example.tuchatapplication.viewmodels.RegisterActivityViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Login : AppCompatActivity(), View.OnClickListener {
    private val TAG = "LoginUser"
    private lateinit var register: TextView
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var btn: MaterialButton
    private lateinit var registerActivityViewModel: RegisterActivityViewModel
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        registerActivityViewModel = ViewModelProvider(this).get(RegisterActivityViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        register = findViewById(R.id.txtRegister)
        email = findViewById(R.id.emailLogin)
        password = findViewById(R.id.passwordLogin)
        btn = findViewById(R.id.btnLogin)
        btn.setOnClickListener(this)
        register.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.txtRegister -> {
                startActivity(Intent(this, Register::class.java))
            }
            R.id.btnLogin -> {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val em = email.text.toString().trim()
        val pass = password.text.toString().trim()

        if (TextUtils.isEmpty(em)){
            email.error = "Email Required"
            email.requestFocus()
        }
        else if (TextUtils.isEmpty(pass)){
            password.error = "Password Required"
            password.requestFocus()
        }
        else{
            btn.isEnabled = false
            var response = registerActivityViewModel.loginUser(em, pass)
            if (response != null){
                btn.isEnabled = true
                if (response.userId != null){
                    sharedPreferences = this.getSharedPreferences(getString(R.string.User), MODE_PRIVATE)
                    var editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                    editor.putString(getString(R.string.firstName), response.firstName)
                    editor.putString(getString(R.string.lastName), response.lastName)
                    editor.putString(getString(R.string.email), response.email)
                    editor.putString(getString(R.string.phone), response.phone)
                    editor.putString(getString(R.string.id), response.userId)
                    editor.apply()
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                    goToMain()

                    Log.i(TAG, "loginUser: ${response.userId}")
                }
                else{
                    Toast.makeText(this, "Login Unsuccessful, check details", Toast.LENGTH_LONG).show()
                }
            }
            else{
                btn.isEnabled = true
                Toast.makeText(this, "Login Unsuccessful, check details", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = this.getSharedPreferences(getString(R.string.User), MODE_PRIVATE)
        var userEmail = sharedPreferences!!.getString(getString(R.string.email), "")
        if (userEmail != null && userEmail != ""){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}