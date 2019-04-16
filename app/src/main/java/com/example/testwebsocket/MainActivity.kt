package com.example.testwebsocket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.testwebsocket.socket.SocketManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val socketManager = SocketManager
        findViewById<Button>(R.id.btn_connect).setOnClickListener {
           socketManager.close()
            socketManager.open()
        }

    }
}
