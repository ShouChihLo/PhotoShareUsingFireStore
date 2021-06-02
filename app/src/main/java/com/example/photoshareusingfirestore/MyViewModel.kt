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

    val newMessage = PostedMessage()

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

        with(newMessage) {
            //text has been set
            //photoUrl has been set via the image picker
            uploader = user!!.displayName
            //timestamp will be set by the server time
        }

        //upload data to the firestore
        collectionRef.add(newMessage)
            .addOnSuccessListener { docRef ->
                    // Build a StorageReference and then upload the image file
                    val uri = Uri.parse(newMessage.photoUrl)
                    val key = docRef.id
                    val storageRef = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)  //get the file name
                    putImageInStorage(storageRef, docRef, uri, key)
                }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error adding document to the database", e)
            }
    }

    private fun putImageInStorage(storageReference: StorageReference, documentRef: DocumentReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        //Asynchronously uploads from a content URI
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        // update the photoUri to the firestore
                        documentRef.update("photoUri", photoUrl)

                        //prepare for the next new posted message
                        newMessage.reset()
                    }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Image upload task was unsuccessful.", e)
            }
    }

}
