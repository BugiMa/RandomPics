package com.example.randompics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.randompics.R
import com.example.randompics.data.remote.Pictures
import com.example.randompics.databinding.ItemPictureBinding
import com.example.randompics.ui.fragment.PictureListFragmentDirections
import com.example.randompics.util.GlideApp

class PictureListAdapter(
    private val context: Context,
    private val pictures: Pictures
) : RecyclerView.Adapter<PictureListAdapter.PictureViewHolder>() {



    inner class PictureViewHolder(private val itemBinding: ItemPictureBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(id: String, url: String?) {

            itemBinding.idText.text = context.getString(R.string.hashed_id, id)

            val progressBar = CircularProgressDrawable(context).apply {
                this.strokeWidth = 16f
                this.centerRadius = 48f
                this.start()
            }

            GlideApp.with(context)
                .load(url)
                .placeholder(progressBar)
                .centerInside()
                .into(itemBinding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val itemBinding = ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pic = pictures[position]
        holder.bind(pic.id, pic.download_url)
        holder.itemView.setOnClickListener {
            val directions = PictureListFragmentDirections
                .actionPictureListFragmentToPictureDetailsFragment(
                    pic.id,
                    pic.author,
                    pic.width,
                    pic.height,
                    pic.download_url
                )
            it.findNavController().navigate(directions)
        }
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    fun updateData(data: Pictures) {
        this.pictures.addAll(data)
        notifyItemRangeInserted(itemCount, data.size)
    }

}