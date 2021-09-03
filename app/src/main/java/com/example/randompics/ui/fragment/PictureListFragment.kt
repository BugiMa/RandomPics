package com.example.randompics.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.randompics.R
import com.example.randompics.adapter.PictureListAdapter
import com.example.randompics.data.remote.Pictures
import com.example.randompics.databinding.FragmentPictureListBinding
import com.example.randompics.ui.activity.MainActivity
import com.example.randompics.ui.viewModel.SharedViewModel
import com.example.randompics.util.Constants.Companion.DOWN
import com.example.randompics.util.Constants.Companion.NETWORK_FAILURE
import com.example.randompics.util.Constants.Companion.NO_INTERNET
import com.example.randompics.util.Resource
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class PictureListFragment : Fragment() {

    private var _binding: FragmentPictureListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).supportActionBar?.title =
            getString(R.string.random_pics)
        _binding = FragmentPictureListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val recyclerView = binding.pictureList
        val fab = binding.refreshFab
        fab.shrink()
        val progressBar = binding.progressBarContainer

        val pictureListAdapter = PictureListAdapter(requireContext(), viewModel.getLoadedPics())

        recyclerView.apply {
            this.adapter = pictureListAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    fab.setMode(
                        (recyclerView.layoutManager!! as LinearLayoutManager).findLastCompletelyVisibleItemPosition() >= recyclerView.adapter!!.itemCount - 1,
                        newState == SCROLL_STATE_IDLE,
                    )
                }
            })
        }



        viewModel.getPics().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {

                    progressBar.visibility = View.GONE
                    response.data?.let {
                        pictureListAdapter.updateData(it)
                        Toast.makeText(requireContext(),"New Images Loaded Successfully!",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    fab.setMode(isExtended = true, isShown = true, R.drawable.ic_baseline_refresh_24, R.string.refresh)
                    response.message?.let { message ->
                        if (message == NO_INTERNET)
                            noInternetDialog() else Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        fab.setOnClickListener {
            viewModel.loadPics()
            fab.setMode(isExtended = false, isShown = true)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun noInternetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Oops!")
            .setIcon(R.drawable.ic_baseline_error_outline_24)
            .setMessage("It looks like You don't have internet connection. Would You like to turn internet on now?")
            .setPositiveButton("Mobile Data") { _, _ ->
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }
            .setNegativeButton("Wi-Fi") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNeutralButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun ExtendedFloatingActionButton.setMode(
        isExtended: Boolean?,
        isShown: Boolean?,
        iconId: Int? = R.drawable.ic_baseline_photo_library_24,
        textId: Int? = R.string.load_more
    ) {
        this.icon = ResourcesCompat.getDrawable(resources ,iconId!!, null)
        this.text = getString(textId!!)
        if (isExtended!!)
            delayed({ this.extend() }) else this.shrink()

        if (isShown!!)
            delayed({ this.show() }) else this.hide()
    }

    private fun delayed(function: () -> Unit, delayMillis: Long? = 400) {
        Handler(Looper.myLooper()!!).postDelayed({
            function()
        }, delayMillis!!)
    }
}