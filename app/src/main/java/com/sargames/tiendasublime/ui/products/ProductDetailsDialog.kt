package com.sargames.tiendasublime.ui.products

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.databinding.DialogProductDetailsBinding
import com.sargames.tiendasublime.models.Product

class ProductDetailsDialog(
    private val product: Product,
    private val onAddToCart: (Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // Configurar datos del producto
        binding.productName.text = product.name
        binding.productPrice.text = "$${product.price}"
        binding.productCategory.text = product.category

        // Cargar imagen
        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.img_splash)
            .error(R.drawable.img_splash)
            .centerCrop()
            .into(binding.productImage)

        var quantity = 1
        binding.tvQuantity.text = quantity.toString()

        // Configurar botones de cantidad
        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnIncrease.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        // Configurar botón de añadir al carrito
        binding.btnAddToCart.setOnClickListener {
            onAddToCart(quantity)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 