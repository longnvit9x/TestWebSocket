package com.example.testwebsocket.socket

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

internal abstract class AbstractConnectionProvider : ConnectionProvider {

    private val mLifecycleStream: PublishSubject<LifecycleEvent> = PublishSubject.create()
    private val mMessagesStream: PublishSubject<String> = PublishSubject.create()

    /**
     * Get socket object.
     * Used for null checking; this object is expected to be null when the connection is not yet established.
     *
     *
     * For example:
     * <pre>
     * return webSocket;
    </pre> *
     */
    internal abstract val socket: Any?

    override fun messages(): Observable<String> {
        return mMessagesStream.startWith(initSocket().toObservable())
    }

    /**
     * Simply close socket.
     *
     *
     * For example:
     * <pre>
     * webSocket.close();
    </pre> *
     */
    internal abstract fun rawDisconnect()

    override fun disconnect(): Completable {
        return Completable
            .fromAction { this.rawDisconnect() }
    }

    private fun initSocket(): Completable {
        return Completable
            .fromAction { this.createWebSocketConnection() }
    }

    // Doesn't do anything at all, only here as a stub
    override fun setHeartbeat(ms: Int): Completable {
        return Completable.complete()
    }

    /**
     * Most important method: connects to websocket and notifies program of messages.
     *
     *
     * See implementations in OkHttpConnectionProvider and WebSocketsConnectionProvider.
     */
    internal abstract fun createWebSocketConnection()

    override fun send(stompMessage: String): Completable {
        return Completable.fromCallable {
            if (socket == null) {
                throw IllegalStateException("Not connected yet")
            } else {
                Log.d(TAG, "Send STOMP menu_message: $stompMessage")
                rawSend(stompMessage)
                return@fromCallable null
            }
        }
    }

    /**
     * Just a simple message send.
     *
     *
     * For example:
     * <pre>
     * webSocket.send(stompMessage);
    </pre> *
     *
     * @param stompMessage message to send
     */
    internal abstract fun rawSend(stompMessage: String)

    fun emitLifecycleEvent(lifecycleEvent: LifecycleEvent) {
        Log.d(TAG, "Emit lifecycle event: " + lifecycleEvent.type.name)
        mLifecycleStream.onNext(lifecycleEvent)
    }

    fun emitMessage(stompMessage: String) {
        Log.d(TAG, "Emit STOMP message: $stompMessage")
        mMessagesStream.onNext(stompMessage)
    }

    override fun lifecycle(): Observable<LifecycleEvent> {
        return mLifecycleStream
    }

    companion object {
        private val TAG = AbstractConnectionProvider::class.java.simpleName
    }
}