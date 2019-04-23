package com.example.testwebsocket.presenter

import com.example.testwebsocket.view.MainView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainPresenter: Presenter<MainView> {
    var view: MainView?= null
    var disposables: CompositeDisposable= CompositeDisposable()
    override fun attachView(view: MainView) {
       this.view= view
    }

    override fun detachView() {
        this.view= null
    }

//    fun requestNewTicket() {
//        var dis: Disposable? = null
//        dis = chatMessageUseCase.requestDeleteRoom(getCurrentOfficeUserId(), editInnerRoom.id!!)
//            .timeout(ConstantMessage.TIME_OUT_REQUEST_SOCKET_LEFT_ROOM, TimeUnit.MILLISECONDS)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe {
//                view?.showLoading(true)
//            }
//            .subscribe({
//                view?.onDeleteRoomSuccess(it.room?.id!!)
//                view?.showLoading(false)
//                isRequestDeleteRoom = false
//                dis?.let { disposables.remove(it) }
//            }, { e ->
//                if (isRoomDeleted){
//                    view?.showLoading(false)
//                    view?.onDeleteRoomSuccess(editInnerRoom.id?:"")
//                }else {
//                    view?.showLoading(false)
//                    view?.handleError(e)
//                }
//                isRequestDeleteRoom = false
//                dis?.let { disposables.remove(it) }
//            })
//        disposables.add(dis)
//    }
}