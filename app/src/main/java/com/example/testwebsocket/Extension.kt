package com.example.testwebsocket

import io.reactivex.CompletableTransformer
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun applySchedulers(): CompletableTransformer {
    return CompletableTransformer { upstream ->
        upstream
            .unsubscribeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> schedulersFlowableTransformer(): FlowableTransformer<T, T> {
    return FlowableTransformer {
        it.unsubscribeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}