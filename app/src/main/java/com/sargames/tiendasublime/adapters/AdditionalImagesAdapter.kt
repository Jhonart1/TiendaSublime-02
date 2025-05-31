package com.sargames.tiendasublime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.databinding.ItemAdditionalImageBinding

class AdditionalImagesAdapter(
    private var imageUrls: List<String>
) : RecyclerView.Adapter<AdditionalImagesAdapter.ImageViewHolder>() {

    private val requestOptions = RequestOptions()
        .placeholder(R.drawable.placeholder_image)
        .error(R.drawable.error_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemAdditionalImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    fun updateImages(newImageUrls: List<String>) {
        imageUrls = newImageUrls
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(
        private val binding: ItemAdditionalImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(binding.ivAdditionalImage)
                .load(imageUrl)
                .apply(requestOptions)
                .into(binding.ivAdditionalImage)
        }
    }
} 