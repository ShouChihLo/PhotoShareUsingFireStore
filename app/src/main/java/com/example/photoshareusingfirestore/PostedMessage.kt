package com.example.photoshareusingfirestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PostedMessage(
    var text: String? = null,
    var photoUrl: String? = null,
    var uploader: String? = null,
    @ServerTimestamp
    var timestamp: Timestamp? = null
) {

    fun reset() {
        this.text = null
        this.photoUrl = null
        this.uploader = null
        this.timestamp = null
    }
}
