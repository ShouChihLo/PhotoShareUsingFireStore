package com.example.photoshareusingfirestore

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.photoshareusingfirestore.databinding.MessageItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

//recyvlerview adapter (inherit from FirestoreRecyclerAdapter)
//options: include data source queried from the database
//each data item is modeled as PostedMessage object
class MessageAdapter(
    private val options: FirestoreRecyclerOptions<PostedMessage>
) : FirestoreRecyclerAdapter<PostedMessage, MessageAdapter.ViewHolder>(options) {

    class ViewHolder(val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostedMessage) {
            binding.tagText.text = item.text
            binding.nameText.text = item.uploader
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MessageItemBinding.inflate(layoutInflater, parent, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.itemView.setOnClickListener {  //display the item's details
            val item = getItem(viewHolder.bindingAdapterPosition)
            it.findNavController()
                .navigate(ShareFragmentDirections.actionShareFragmentToShowFragment(
                    item.text!!, item.timestamp!!.toDate().toString(), item.photoUrl!!))
        }

        return viewHolder
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: PostedMessage
    ) {
        holder.bind(model)  //model = options.snapshots[position]
    }

//    override fun onDataChanged() {
//        super.onDataChanged()
//        // solve the problem: select picture then crash (solution2)
//        notifyDataSetChanged()
//    }

}