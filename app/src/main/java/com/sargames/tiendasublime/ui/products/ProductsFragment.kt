package com.sargames.tiendasublime.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.adapters.ProductAdapter
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.DialogProductDetailsBinding
import com.sargames.tiendasublime.databinding.FragmentProductsBinding
import com.sargames.tiendasublime.models.Product
import androidx.appcompat.app.AlertDialog

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var productAdapter: ProductAdapter
    private var cartUpdateListener: OnCartUpdateListener? = null

    interface OnCartUpdateListener {
        fun onCartUpdated()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        sharedPrefs = SharedPreferencesManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadProducts()
    }

    private fun setupUI() {
        // Configurar opciones de Glide para el adaptador
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()

        productAdapter = ProductAdapter(emptyList(), requestOptions) { product ->
            navigateToProductDetail(product.id)
        }

        binding.productsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }

        // Configurar chips de categorías
        val categories = listOf("Todos", "Clásico", "Vintage", "Moderno", "Artístico", "Geek", "Minimalista", "Bohemio", "Industrial", "Naturaleza", "Personalizado")
        categories.forEach { category ->
            val chip = Chip(context).apply {
                text = category
                isCheckable = true
                if (category == "Todos") isChecked = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        binding.categoryChipGroup.clearCheck()
                        this@apply.isChecked = true
                        filterProducts(category)
                    }
                }
            }
            binding.categoryChipGroup.addView(chip)
        }

        // Configurar búsqueda
        binding.searchLayout.setEndIconOnClickListener {
            val query = binding.searchLayout.editText?.text?.toString() ?: ""
            filterProductsBySearch(query)
        }

        binding.searchLayout.editText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchLayout.editText?.text?.toString() ?: ""
            filterProductsBySearch(query)
            true
        }
    }

    private fun loadProducts() {
        val products = dbHelper.getAllProducts()
        productAdapter.updateProducts(products)
    }

    private fun filterProducts(category: String) {
        val products = if (category == "Todos") {
            dbHelper.getAllProducts()
        } else {
            dbHelper.getProductsByCategory(category)
        }
        productAdapter.updateProducts(products)
    }

    private fun filterProductsBySearch(query: String) {
        val products = if (query.isEmpty()) {
            dbHelper.getAllProducts()
        } else {
            dbHelper.searchProducts(query)
        }
        productAdapter.updateProducts(products)
    }

    private fun navigateToProductDetail(productId: Int) {
        val action = ProductsFragmentDirections
            .actionNavigationProductsToProductDetail(productId)
        findNavController().navigate(action)
    }

    fun setOnCartUpdateListener(listener: OnCartUpdateListener) {
        cartUpdateListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 