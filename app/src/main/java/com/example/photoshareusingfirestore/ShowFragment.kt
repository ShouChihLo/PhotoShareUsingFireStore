package com.example.photoshareusingfirestore

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoshareusingfirestore.databinding.FragmentShowBinding

class ShowFragment : Fragment() {

    private lateinit var binding: FragmentShowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show, container, false)

        //retrieve the passed argument
        val args = ShowFragmentArgs.fromBundle(requireArguments())

        binding.descriptionText.text = args.tag
        binding.dateText.text = args.date
        //load the image file from the firebase storage
        Glide.with(binding.imageView)
            .load(Uri.parse(args.imageUri))
            .apply(RequestOptions().centerCrop())
            .into(binding.imageView)

        return binding.root
    }

}