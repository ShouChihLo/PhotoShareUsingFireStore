package com.example.photoshareusingfirestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PostedMessage(
    var text: String? = null,
    var photoUrl: String? = null,
    var uploader: String? = null,
    @ServerTimestamp
    var timestamp: Timestamp? = null
)

//class MyScrollToBottomObserver(
//    private val recycler: RecyclerView,
//    private val adapter: MessageAdapter,
//    private val manager: LinearLayoutManager
//) : RecyclerView.AdapterDataObserver() {
//    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//        super.onItemRangeInserted(positionStart, itemCount)
//        val count = adapter.itemCount
//        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
//        // If the recycler view is initially being loaded or the
//        // user is at the bottom of the list, scroll to the bottom
//        // of the list to show the newly added message.
//        val loading = lastVisiblePosition == -1
//        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
//        if (loading || atBottom) {
//            recycler.scrollToPosition(positionStart)
//        }
//    }
//}
