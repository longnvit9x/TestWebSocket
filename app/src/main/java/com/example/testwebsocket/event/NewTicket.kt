package com.example.testwebsocket.event

import com.google.gson.annotations.SerializedName

class NewTicket : MessageEvent() {

    override fun commandName(): String {
        return "newticket"
    }

    @SerializedName("pticketid")
    var id: String? = null
    @SerializedName("pname")
    var name: String? = null
    @SerializedName("pnote")
    var note: String? = null
    @SerializedName("pcustomerid")
    var customerId: String? = null
    @SerializedName("ptotalproductprice")
    var totalProductPrice: String? = null
    @SerializedName("ptaxamount")
    var taxAmount: String? = null
    @SerializedName("ptotaldiscount")
    var totalDiscount: String? = null
    @SerializedName("ptotalamount")
    var totalAmount: String? = null
    @SerializedName("pposmachineid")
    var posMachineid: String? = null
    @SerializedName("pposgroupcode")
    var posGroupCode: String? = null
    @SerializedName("pposcode")
    var posCode: String? = null
    @SerializedName("ptag")  // tag1,tag2,tag3
    var tagIds: String? = null
    @SerializedName("pUserId")
    var userId: String? = null
    @SerializedName("pUserIp")
    var userIp: String? = null

    @SerializedName("pitem_id")
    var itemId: String? = null
    @SerializedName("pitem_price_id")
    var itemPriceId: String? = null
    @SerializedName("pquantity")
    var itemQuantity: String? = null
    @SerializedName("pprice")
    var itemPrice: String? = null
    @SerializedName("pdiscount")
    var itemDiscount: String? = null
    @SerializedName("ptaxamount_item")
    var itemTaxAmount: String? = null
    @SerializedName("pnote_item")
    var itemNote: String? = null
    @SerializedName("pitem_type")
    var itemType: String? = null
    @SerializedName("pitem_quantity_free")
    var itemQuantityFree: String? = null
    @SerializedName("pdac_thu")
    var itemDacthu: String? = null
    @SerializedName("pstatus")
    var itemStatus: String? = null
}