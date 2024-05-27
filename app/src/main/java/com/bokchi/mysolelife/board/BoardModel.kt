package com.bokchi.mysolelife.board

import com.bokchi.mysolelife.utils.FBRef

data class BoardModel (
    val title: String = "",
    val content: String = "",
    val uid: String = "",
    val time: String = "",
    val reviewtitle: String = "",
    val author: String = "",
    val rating: Float = 0.0f,
    val genre: String = "" // 장르 필드 추가
)
