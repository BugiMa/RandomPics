package com.example.randompics.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.randompics.R
import com.example.randompics.databinding.FragmentPictureDetailsBinding
import com.example.randompics.ui.activity.MainActivity
import com.example.randompics.ui.viewModel.SharedViewModel
import com.example.randompics.util.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureDetailsFragment : Fragment() {

    private var _binding: FragmentPictureDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedViewModel

    private val args: PictureDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.picture_info, args.id)
        _binding = FragmentPictureDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val imageView = binding.imageView
        val progressBar = CircularProgressDrawable(requireContext()).apply {
            this.strokeWidth = 8f
            this.centerRadius = 64f
            this.start()
        }

        GlideApp.with(this)
            .load(args.downloadUrl)
            .placeholder(progressBar)
            .centerInside()
            .into(imageView)

        val authorTextView = binding.author
        val sizeTextView = binding.size
        val downloadUlrTextView = binding.downloadUrl

        //TODO: BUNDLE

        authorTextView.text = getString(R.string.author, args.author)
        sizeTextView.text = getString(R.string.size, args.width.toString(), args.height.toString())
        downloadUlrTextView.text = getString(R.string.download_url, args.downloadUrl)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}