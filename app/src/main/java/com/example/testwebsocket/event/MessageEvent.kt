package com.example.testwebsocket.event

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

open class MessageEvent {

    companion object {
        fun fromJson(json: String?): MessageEvent {
            val tmp = GsonBuilder().create().fromJson(json, MessageEvent::class.java)
            when (tmp.type) {
                "newTicket" -> return GsonBuilder().create().fromJson(json, NewTicket::class.java)
//                "room_deleted" -> return GsonBuilder().create().fromJson(json, RoomDeleted::class.java)
//                "room_joined" -> return GsonBuilder().create().fromJson(json, RoomJoined::class.java)
//                "user_invited" -> return GsonBuilder().create().fromJson(json, UserInvited::class.java)
//                "room_left" -> return GsonBuilder().create().fromJson(json, RoomLeft::class.java)
//                "room_edited" -> return GsonBuilder().create().fromJson(json, RoomEdited::class.java)
//                "rooms_listed" -> return GsonBuilder().create().fromJson(json, RoomListed::class.java)
//                "member_listed" -> return GsonBuilder().create().fromJson(json, MemberListed::class.java)
//                "message_sent" -> return GsonBuilder().create().fromJson(json, MessageSent::class.java)
//                "tmp_text_sent" -> return GsonBuilder().create().fromJson(json, TmpTextSent::class.java)
//                "starred" -> return GsonBuilder().create().fromJson(json, Starred::class.java)
//                "message_marked" -> return GsonBuilder().create().fromJson(json, MessageMarked::class.java)
//                "unread_refreshed" -> return GsonBuilder().create().fromJson(json, UnreadRefreshed::class.java)
//                "user_typing" -> return GsonBuilder().create().fromJson(json, UserTyping::class.java)
//                "message_listed" -> return GsonBuilder().create().fromJson(json, MessageListed::class.java)
//                "notify_edited" -> return GsonBuilder().create().fromJson(json, NotifyEdited::class.java)
//                "notify_got" -> return GsonBuilder().create().fromJson(json, NotifyGot::class.java)
//                "attachment_listed" -> return GsonBuilder().create().fromJson(json, AttachmentListed::class.java)
//                "attachment_forwarded" -> return GsonBuilder().create().fromJson(json, AttachmentForwarded::class.java)
//                "attachment_deleted" -> return GsonBuilder().create().fromJson(json, AttachmentDeleted::class.java)
//                "modified_listed" -> return GsonBuilder().create().fromJson(json, ModifiedListed::class.java)
//                "room_got" -> return GsonBuilder().create().fromJson(json, RoomGot::class.java)
//                "tmp_text_got" -> return GsonBuilder().create().fromJson(json, TmpTextGot::class.java)
//                "user_refreshed" -> return MessageEvent()
//                "error" -> return GsonBuilder().create().fromJson(json, Error::class.java)
                else -> throw IllegalArgumentException("unknown type")
            }
        }
    }

    @SerializedName("act")
    open var type: String? = commandName()

    @SerializedName("request_id")
    var requestId: String? = null

    @SerializedName("ts")
    var ts: Long? = null

    fun toJson(): String {
        return GsonBuilder().create().toJson(this)
    }

    open fun commandName(): String {
        return ""
    }
}