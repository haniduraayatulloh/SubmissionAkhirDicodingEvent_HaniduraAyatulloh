package com.example.dicodingevent.data.model

import android.os.Parcelable
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventItem(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("owner")
    val ownerName: String? = null,

    @field:SerializedName("begin_time")
    val beginTime: String? = null,

    // Field gambar baru
    @field:SerializedName("imageLogo")
    val imageLogo: String? = null,

    @field:SerializedName("mediaCover")
    val mediaCover: String? = null,

    @field:SerializedName("summary")
    val summary: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("link")
    val link: String? = null,

    @field:SerializedName("quota")
    val quota: Int? = null,

    @field:SerializedName("registrants")
    val registered: Int? = null,

    var isFavorite: Boolean = false
) : Parcelable {

    fun getImageUrl(): String? {
        return mediaCover.takeIf { !it.isNullOrEmpty() } ?: imageLogo.takeIf { !it.isNullOrEmpty() }
    }

    fun isFull(): Boolean {
        return if (quota != null && registered != null) registered >= quota else false
    }

    fun getRemainingQuota(): Int? {
        return if (quota != null && registered != null) quota - registered else null
    }
}

fun FavoriteEventEntity.toEventItem(): EventItem {
    return EventItem(
        id = this.id,
        name = this.name,
        ownerName = this.ownerName,
        beginTime = this.beginTime,
        mediaCover = this.mediaCover,
        imageLogo = this.imageLogo,
        summary = this.summary,
        description = this.description,
        link = this.link,
        isFavorite = true,
        quota = null,
        registered = null
    )
}

fun EventItem.toFavoriteEntity(): FavoriteEventEntity {
    return FavoriteEventEntity(
        id = this.id,
        name = this.name,
        ownerName = this.ownerName,
        beginTime = this.beginTime,
        mediaCover = this.mediaCover,
        imageLogo = this.imageLogo,
        summary = this.summary,
        description = this.description,
        link = this.link
    )
}