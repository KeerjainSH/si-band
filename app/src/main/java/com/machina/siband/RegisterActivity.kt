package com.machina.siband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.machina.siband.admin.AdminActivity
import com.machina.siband.databinding.ActivityRegisterBinding
import com.machina.siband.model.Account
import com.machina.siband.repository.AdminFirestoreRepo
import com.machina.siband.user.UserActivity

class RegisterActivity : AppCompatActivity() {

  private var _binding: ActivityRegisterBinding? = null
  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupArrayAdapter()
    setResult(20)

    binding.registerButton.setOnClickListener {
      validateRegister()
    }
  }

  private fun validateRegister() {
    val email = binding.registerEmail.editText?.text.toString()
    val password = binding.registerPassword.editText?.text.toString()
    val confirmPassword = binding.registerConfirmPassword.editText?.text.toString()
    val nama = binding.registerNama.editText?.text.toString()
    val tipeAkun = binding.registerTipeAkun.editText?.text.toString()

    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nama.isEmpty() || tipeAkun.isEmpty()) {
      Toast.makeText(this, "Please fill all the field", Toast.LENGTH_SHORT).show()
      return
    }

    if (password != confirmPassword) {
      Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show()
      return
    }

    val account = Account(email, nama, password, tipeAkun)
    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(account.email, account.password)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          AdminFirestoreRepo.getAccountRef(account.email)
            .set(account)
            .addOnSuccessListener {
              when (account.tipeAkun) {
                "Admin" -> {
//                                val intent = Intent(this, AdminActivity::class.java)
//                                startActivityForResult(intent, 200)
                  setResult(501)
                  finish()
                }
                "User" -> {
//                                val intent = Intent(this, UserActivity::class.java)
//                                startActivityForResult(intent, 200)
                  setResult(502)
                  finish()
                }
                else -> {
                  Toast.makeText(
                    this,
                    "Unknown error occured please try again later",
                    Toast.LENGTH_LONG
                  ).show()
                }
              }
            }
        } else {
          Toast.makeText(
            this,
            "${task.exception}",
            Toast.LENGTH_LONG
          ).show()
        }
      }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 200 && resultCode == 1000) {
      setResult(101)
      finish()
    }
  }

  private fun setupArrayAdapter() {
    val tipeAkun = resources.getStringArray(R.array.akun)
    val mArrayAdapter = ArrayAdapter(this, R.layout.item_list_dropdown, tipeAkun)
    (binding.registerTipeAkun.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}