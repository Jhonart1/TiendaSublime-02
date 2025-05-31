package com.sargames.tiendasublime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.databinding.ItemCartProductBinding
import com.sargames.tiendasublime.models.CartItem

class CartAdapter(
    private var cartItems: List<CartItem> = emptyList(),
    private val onQuantityChanged: (CartItem) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.tvProductName.text = cartItem.name
            binding.tvProductPrice.text = "$${String.format("%.2f", cartItem.price)}"
            binding.tvQuantity.text = cartItem.quantity.toString()

            // Cargar imagen del producto
            Glide.with(binding.root)
                .load(cartItem.imageUrl)
                .placeholder(R.drawable.img_splash)
                .error(R.drawable.img_splash)
                .centerCrop()
                .into(binding.ivProductImage)

            // Configurar botones de cantidad
            binding.btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                    onQuantityChanged(cartItem)
                }
            }

            binding.btnIncrease.setOnClickListener {
                cartItem.quantity++
                onQuantityChanged(cartItem)
            }

            // Configurar botón de eliminar
            binding.btnRemove.setOnClickListener {
                onRemoveItem(cartItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
} 