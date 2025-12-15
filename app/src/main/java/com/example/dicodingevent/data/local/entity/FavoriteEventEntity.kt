package com.example.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event")
data class FavoriteEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "owner_name")
    val ownerName: String? = null,

    @ColumnInfo(name = "begin_time")
    val beginTime: String? = null,

    @ColumnInfo(name = "media_cover")
    val mediaCover: String? = null,

    @ColumnInfo(name = "image_logo")
    val imageLogo: String? = null,

    @ColumnInfo(name = "summary")
    val summary: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "link")
    val link: String? = null
)