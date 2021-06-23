package com.machina.siband

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.machina.siband.admin.AdminActivity
import com.machina.siband.databinding.ActivityMainBinding
import com.machina.siband.model.Account
import com.machina.siband.model.Account.Companion.toAccount
import com.machina.siband.repository.AdminFirestoreRepo
import com.machina.siband.user.UserActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resolveLogin()


        binding.mainLogin.setOnClickListener {
            onLogin()
        }

        binding.mainRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }

    private fun resolveLogin() {
        val auth = Firebase.auth
        val currUser = auth.currentUser

        if (currUser != null) {
            val email = currUser.email
            if (email != null) {
                resolveTipeAkun(email)
            }

        }

    }

    private fun onLogin() {
        val email = binding.mainEmail.editText?.text.toString()
        val password = binding.mainPassword.editText?.text.toString()
        val auth = Firebase.auth

        Log.d(TAG, "email [$email] password [$password]")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resolveTipeAkun(email)
                } else {
                    Log.d(TAG, "${task.exception}")
                    Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resolveTipeAkun(email: String) {
        AdminFirestoreRepo.getAccountRef(email)
            .get()
            .addOnSuccessListener {
                val res = it.toAccount()
                when (res?.tipeAkun) {
                    "Admin" -> {
                        val intent = Intent(this, AdminActivity::class.java)
                        startActivityForResult(intent, 200)
                    }
                    "User" -> {
                        val intent = Intent(this, UserActivity::class.java)
                        startActivityForResult(intent, 200)
                    }
                    else -> {
                        Toast.makeText(this, "Unknown error occured please try again later", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "result code [$resultCode]")
        if (requestCode == 200 && resultCode == 0) {
            finish()
        } else {
            binding.mainEmail.editText?.setText("")
            binding.mainPassword.editText?.setText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
        const val PREF_LOGIN = "main.PreferenceLogin"
        const val PREF_EMAIL = "main.PreferenceEmail"
        const val PREF_PASSWORD = "main.PreferencePassword"
        const val PREF_IS_REMEMBER = "main.isRemember"
    }
}
