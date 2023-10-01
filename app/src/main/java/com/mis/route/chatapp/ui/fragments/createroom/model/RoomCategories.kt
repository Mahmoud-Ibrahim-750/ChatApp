package com.mis.route.chatapp.ui.fragments.createroom.model

import com.mis.route.chatapp.R
import com.mis.route.chatapp.ui.model.RoomCategory

object RoomCategories {
    fun getCategories() = listOf(
        RoomCategory(1, "Sports", R.drawable.image_sports_cat),
        RoomCategory(2, "Music", R.drawable.image_music_cat),
        RoomCategory(3, "Movies", R.drawable.image_movies_cat)
    )
}