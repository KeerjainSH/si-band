package com.machina.siband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.machina.siband.user.UserActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }
}
