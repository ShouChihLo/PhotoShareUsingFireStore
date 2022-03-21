package com.example.photoshareusingfirestore

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MyViewModel : ViewModel() {

    companion object {
        private const val TAG = "MyViewModel"
    }

    // Initialize FireStore Database
    private val db = Firebase.firestore
    val collectionRef = db.collection("messages")

    var photoUrl: String? = null
    var messageContent: String? = null

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = LoginStateLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun uploadMessage() {
        val user = FirebaseAuth.getInstance().currentUser
        val message = PostedMessage(messageContent, photoUrl,user!!.displayName, null)

        // write the message to the firestore
        collectionRef.add(message)
            .addOnSuccessListener { docRef ->
                // Build a StorageReference and then upload the image file
                val uri = Uri.parse(message.photoUrl)
                val key = docRef.id
                val storageRef = Firebase.storage
                    .getReference(user!!.uid)
                    .child(key)
                    .child(uri.lastPathSegment!!)  //get the file name
                putImageInStorage(storageRef, uri, docRef)
                // reset old message
                photoUrl = ""
                messageContent = ""
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error adding document to the firestore", e)
            }
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, docRef: DocumentReference){
        // Upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot -> // Get the public downloadUrl for the image
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        // update photoUrl to the downloadUrl in the firestore
                        val photoUri = uri.toString()
                        docRef.update( "photoUri", photoUri)
                    }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Image upload task was unsuccessful.", e)
            }
    }

}
