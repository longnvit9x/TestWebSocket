package com.example.testwebsocket.socket

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.exceptions.InvalidDataException
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import java.nio.charset.Charset
import java.util.*
import javax.net.ssl.SSLContext

internal class WebSocketsConnectionProvider constructor(private val mUri: String, var mConnectHttpHeaders: MutableMap<String, String> = mutableMapOf()) : AbstractConnectionProvider() {
    override val socket: Any?
        get() = mWebSocketClient

    private var mWebSocketClient: WebSocketClient? = null
    private var haveConnection: Boolean = false
    private var mServerHandshakeHeaders: MutableMap<String, String>? = null
    override fun rawDisconnect() {
        mWebSocketClient?.close()
    }

    override fun createWebSocketConnection() {
        if (haveConnection)
            throw IllegalStateException("Already have connection to web socket")

        mWebSocketClient = object : WebSocketClient(URI.create(mUri), Draft_6455(), mConnectHttpHeaders, 0) {

            //            @Throws(InvalidDataException::class)
            override fun onWebsocketHandshakeReceivedAsClient(conn: WebSocket?, request: ClientHandshake?, response: ServerHandshake) {
                Log.d(TAG, "onWebsocketHandshakeReceivedAsClient with response: " + response.httpStatus + " " + response.httpStatusMessage)
                mServerHandshakeHeaders = TreeMap()
                val keys = response.iterateHttpFields()
                while (keys.hasNext()) {
                    val key = keys.next()
                    mServerHandshakeHeaders!![key] = response.getFieldValue(key)
                }
            }

            override fun onOpen(handshakeData: ServerHandshake) {
                Log.d(TAG, "onOpen with handshakeData: " + handshakeData.httpStatus + " " + handshakeData.httpStatusMessage)
                val openEvent = LifecycleEvent(LifecycleEvent.Type.OPENED)
                openEvent.handshakeResponseHeaders = mServerHandshakeHeaders!!
                emitLifecycleEvent(openEvent)
            }

            override fun onMessage(message: String) {
                Log.d(TAG, "onMessage: $message")
                emitMessage(message)
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                Log.d(TAG, "onClose: code=$code reason=$reason remote=$remote")
                haveConnection = false
                emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.CLOSED))
            }

            override fun onError(ex: Exception) {
                Log.e(TAG, "onError", ex)
                emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.ERROR, ex))
            }


            private val messBuf = StringBuilder()
            @Suppress("OverridingDeprecatedMember")
            override fun onWebsocketMessageFragment(conn: WebSocket?, frame: Framedata?) {
                Log.d(TAG, "onWebsocketMessageFragment")
                try {
                    var s = String(frame?.payloadData!!.array(), Charset.forName("UTF-8"))
                    s = s.trim()
                    messBuf.append(s)
                    if (s.contains("\u0000")) {
                        emitMessage(messBuf.toString())
                        messBuf.setLength(0)
                    }
                } catch (e: InvalidDataException) {
                    Timber.e("xyz--onWebsocketMessageFragment--" + e.message)
                    e.printStackTrace()
                }

            }
        }

        if (mUri.startsWith("wss")) {
            try {
                val sc = SSLContext.getInstance("TLS")
                sc.init(null, null, null)
                val factory = sc.socketFactory
                mWebSocketClient!!.socket = factory.createSocket()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        mWebSocketClient?.connect()
        haveConnection = true
    }

    override fun rawSend(stompMessage: String) {
        try {
            mWebSocketClient?.send(stompMessage)
        } catch (e: Exception) {
            emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.ERROR, e))
        }
    }

    companion object {

        private val TAG = WebSocketsConnectionProvider::class.java.simpleName
    }
}
