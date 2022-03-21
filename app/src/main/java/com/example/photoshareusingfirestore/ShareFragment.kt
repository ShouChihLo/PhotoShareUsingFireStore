package com.example.photoshareusingfirestore

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoshareusingfirestore.databinding.FragmentShareBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ShareFragment : Fragment() {

    companion object {
        private const val TAG = "SharedFragment"
    }

    private lateinit var adapter: MessageAdapter
    private lateinit var binding: FragmentShareBinding
    private lateinit var viewModel: MyViewModel
    //deal with the return of image picker
    val imagePickUpResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.photoUrl = uri.toString()
    }
    //deal with the return of firebase UI authentication
    val authResult = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        if (result.resultCode == RESULT_OK)
            Log.d(TAG, "login success")
        else
            Log.d(TAG, "login fail")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        //read data from the firebase
        val query = viewModel.collectionRef.orderBy("timestamp")

        val options = FirestoreRecyclerOptions.Builder<PostedMessage>()
            .setQuery(query, PostedMessage::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        //configure the recyclerview
        adapter = MessageAdapter(options)
        val manager = LinearLayoutManager(this.activity)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this.activity, DividerItemDecoration.VERTICAL))
        // solve the problem: select picture then crash (solution1)
        binding.recyclerView.itemAnimator = null

        //adapter.registerAdapterDataObserver(
        //    MyScrollToBottomObserver(binding.recyclerView, adapter, manager)
        //)

        //enable the photo pickup button
        binding.selectButton.setOnClickListener {
            imagePickUpResult.launch("image/*")
        }

        //enable the send message button
        binding.sendButton.setOnClickListener {
            viewModel.messageContent = binding.editDescription.text.toString()
            hideKeyboard()
            if (checkMessage()) {
                viewModel.uploadMessage()
                binding.editDescription.setText("")
            }
        }

        //enable options menu
        setHasOptionsMenu(true)
        return binding.root
    }


    // called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()  //monitor the login state
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            if (it != MyViewModel.AuthenticationState.AUTHENTICATED)
                launchSignIn()
        })
    }

    private fun launchSignIn() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity
        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        authResult.launch(loginIntent)

    }

    private fun checkMessage(): Boolean {
        return if (viewModel.photoUrl.isNullOrEmpty() || viewModel.messageContent.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "select one photo with an annotated text",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else
            true
    }

    fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> AuthUI.getInstance().signOut(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onPause() {
//        adapter.stopListening()  //stop reading data from the database
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        adapter.startListening()  //star reading data from the database
//    }

}