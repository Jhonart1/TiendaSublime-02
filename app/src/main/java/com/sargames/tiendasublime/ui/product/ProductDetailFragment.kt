package com.sargames.tiendasublime.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.adapters.AdditionalImagesAdapter
import com.sargames.tiendasublime.adapters.Comment
import com.sargames.tiendasublime.adapters.CommentsAdapter
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.databinding.FragmentProductDetailBinding
import com.sargames.tiendasublime.models.Product

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var dbHelper: DatabaseHelper
    private var currentProduct: Product? = null
    private lateinit var additionalImagesAdapter: AdditionalImagesAdapter
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdditionalImagesRecyclerView()
        setupCommentsRecyclerView()
        loadProductData()
        setupListeners()
    }

    private fun setupAdditionalImagesRecyclerView() {
        additionalImagesAdapter = AdditionalImagesAdapter(emptyList())
        binding.rvAdditionalImages.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = additionalImagesAdapter
        }
    }

    private fun setupCommentsRecyclerView() {
        commentsAdapter = CommentsAdapter(emptyList())
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentsAdapter
            // Añadir un divisor si es necesario (opcional)
            // addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun loadProductData() {
        // Cargar datos del producto desde la base de datos
        currentProduct = dbHelper.getProductById(args.productId)
        currentProduct?.let { product ->
            binding.apply {
                tvProductTitle.text = product.name
                tvProductDescription.text = product.description
                tvProductPrice.text = "$${product.price}"
                
                // Configurar opciones de Glide para usar fitCenter
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()

                // Cargar imagen principal
                Glide.with(requireContext())
                    .load(product.imageUrl)
                    .apply(requestOptions)
                    .into(ivProductImage)

                // Cargar imágenes adicionales (usando la misma imagen principal 4 veces por ahora)
                val additionalImageUrls = List(4) { product.imageUrl }
                additionalImagesAdapter.updateImages(additionalImageUrls)

                // Cargar comentarios de ejemplo (sin funcionalidad real)
                val exampleComments = listOf(
                    Comment("Usuario1", "¡Excelente producto! Muy recomendable."),
                    Comment("Usuario2", "Me llegó rápido y en perfecto estado.", "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50"),
                    Comment("Usuario3", "El diseño es tal cual la foto, muy contento.")
                )
                commentsAdapter.updateComments(exampleComments)
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddToCart.setOnClickListener {
            currentProduct?.let { product ->
                // Navegar a la pantalla de personalización
                val action = ProductDetailFragmentDirections
                    .actionNavigationProductDetailToCustomization(product.id)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 