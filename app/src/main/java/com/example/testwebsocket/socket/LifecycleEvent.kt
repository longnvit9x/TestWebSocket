package com.example.testwebsocket.socket

class LifecycleEvent constructor(val type: Type, val exception: Exception? = null, val message: String? = null) {

    var handshakeResponseHeaders = mutableMapOf<String, String>()

    enum class Type {
        OPENED, CLOSED, ERROR
    }
}