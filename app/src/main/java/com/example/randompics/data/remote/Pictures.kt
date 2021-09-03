package com.example.randompics.data.remote


class Pictures : ArrayList<Pictures.Picture>() {
    data class Picture(
        val author: String,
        val download_url: String,
        val height: Int,
        val id: String,
        val url: String,
        val width: Int
    )
}