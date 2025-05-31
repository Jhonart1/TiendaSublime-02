package com.sargames.tiendasublime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.databinding.ItemCommentBinding

data class Comment(
    val username: String,
    val commentText: String,
    val userProfileImageUrl: String? = null // URL opcional de la imagen de perfil
)

class CommentsAdapter(
    private var comments: List<Comment>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.apply {
                tvUsername.text = comment.username
                tvCommentText.text = comment.commentText

                // Cargar imagen de perfil (usando un placeholder por ahora si no hay URL)
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)

                Glide.with(ivUserProfile)
                    .load(comment.userProfileImageUrl)
                    .apply(requestOptions)
                    .into(ivUserProfile)
            }
        }
    }
} 