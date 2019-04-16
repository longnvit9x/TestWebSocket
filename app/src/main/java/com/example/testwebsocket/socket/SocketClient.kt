package com.example.testwebsocket.socket

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class SocketClient(private val mConnectionProvider: ConnectionProvider) {

    private val tag = SocketClient::class.java.simpleName
    private var mTopics: ConcurrentHashMap<String, String>? = null
    var isConnected: Boolean = false
        private set(connected) {
            field = connected
            mConnectionStream.onNext(isConnected)
        }
    var isConnecting: Boolean = false
        private set
    private var legacyWhitespace: Boolean = false

    private val mMessageStream: PublishSubject<String> = PublishSubject.create()
    private lateinit var mStreamMap: Flowable<String>
    private val mConnectionStream: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    private var parser: Parser? = null
    private var mLifecycleDisposable: Disposable? = null
    private var mMessagesDisposable: Disposable? = null
    private lateinit var mHeaders: SocketHeader
    private var heartbeat: Int = 0

    init {
        parser = Parser.NONE
    }

    enum class Parser {
        NONE,
        RABBITMQ
    }

    fun setParser(parser: Parser) {
        this.parser = parser
    }

    fun setHeartbeat(ms: Int) {
        heartbeat = ms
        mConnectionProvider.setHeartbeat(ms).subscribe()
    }

    var lifecycleEventCallback: (LifecycleEvent.Type) -> Unit = {}
    @JvmOverloads
    fun connect(_headers: SocketHeader, lifecycleCallback: (LifecycleEvent.Type) -> Unit) {
        mHeaders = _headers
        if (isConnected) return
        mLifecycleDisposable = mConnectionProvider.lifecycle()
            .subscribe { lifecycleEvent ->
                lifecycleEventCallback = lifecycleCallback
                lifecycleCallback.invoke(lifecycleEvent.type)
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d(TAG, _headers.value)
                        mConnectionProvider.send("{\"act\":\"login\",\"user\":\"longnvneo\"}")
                            .subscribe()
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        isConnected = false
                        isConnecting = false
                    }

                    LifecycleEvent.Type.ERROR -> {
                        isConnected = false
                        isConnecting = false
                    }
                }
            }

        isConnecting = true
        mMessagesDisposable = mConnectionProvider.messages()
            .doOnNext { this.callSubscribers(it) }
            .filter { msg -> msg == SocketCommon.CONNECTED }
            .subscribe {
                isConnected = true
                isConnecting = false
            }
    }

    fun reconnect() {
        disconnectCompletable()
            .subscribe({
                Timber.e("---------" + mHeaders.value)
                connect(mHeaders, lifecycleEventCallback)
            }
            ) { e -> Log.e(tag, "Disconnect error", e) }
    }

    fun send(stompMessage: String): Completable {
        val completable = mConnectionProvider.send(stompMessage)
        val connectionComplete = mConnectionStream
            .filter { isConnected -> isConnected }
            .firstOrError().toCompletable()
        return completable
            .startWith(connectionComplete)
    }

    private fun callSubscribers(stompMessage: String) {
        mMessageStream.onNext(stompMessage)
    }

    fun lifecycle(): Flowable<LifecycleEvent> {
        return mConnectionProvider.lifecycle().toFlowable(BackpressureStrategy.BUFFER)
    }

    fun disconnect() {
        Timber.e("xyz--disconnect--")
        disconnectCompletable().subscribe({ Timber.e("DrjoyApplication--------Disconnect socket success") }) { e ->
            Log.e(
                tag,
                "DrjoyApplication-------Disconnect error",
                e
            )
        }
    }

    private fun disconnectCompletable(): Completable {
        Timber.e("xyz--disconnectCompletable--")
        mLifecycleDisposable?.dispose()
        mMessagesDisposable?.dispose()
        return mConnectionProvider.disconnect()
            .doOnComplete { isConnected = false }
    }

    fun onMessage(): Flowable<String> {
        mStreamMap = mMessageStream
            .toFlowable(BackpressureStrategy.BUFFER)
            .share()

        return mStreamMap
    }

    /**
     * Reverts to the old frame formatting, which included two newlines between the message body
     * and the end-of-frame marker.
     *
     *
     * Legacy: Body\n\n^@
     *
     *
     * Default: Body^@
     *
     * @param legacyWhitespace whether to append an extra two newlines
     * @see [The STOMP spec](http://stomp.github.io/stomp-specification-1.2.html.STOMP_Frames)
     */
    fun setLegacyWhitespace(legacyWhitespace: Boolean) {
        this.legacyWhitespace = legacyWhitespace
    }


    companion object {

        private val TAG = SocketClient::class.java.simpleName

        const val SUPPORTED_VERSIONS = "1.1,1.0"
        const val DEFAULT_ACK = "auto"
    }
}
