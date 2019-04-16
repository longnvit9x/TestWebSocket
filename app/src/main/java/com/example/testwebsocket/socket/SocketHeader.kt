package com.example.testwebsocket.socket

class SocketHeader (val act: String, val value: String) {
    companion object {

        const val VERSION = "version"
        const val HEART_BEAT = "heart-beat"
        const val DESTINATION = "destination"
        const val CONTENT_TYPE = "content-type"
        const val MESSAGE_ID = "message-id"
        const val ID = "id"
        const val ACK = "ack"
    }
}