package com.sargames.tiendasublime.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sargames.tiendasublime.adapters.ProductAdapter
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.databinding.FragmentHomeBinding
import com.sargames.tiendasublime.models.Product

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        dbHelper = DatabaseHelper(requireContext())
        setupRecyclerViews()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        // Obtener productos de la base de datos
        val offersProducts = dbHelper.getOffersProducts()
        val featuredProducts = dbHelper.getFeaturedProducts()
        val catalogProducts = dbHelper.getAllProducts()

        // Configurar RecyclerView de ofertas
        binding.offersRecyclerView.apply {
            adapter = ProductAdapter(offersProducts) { product ->
                navigateToProductDetail(product.id)
            }
        }

        // Configurar RecyclerView de destacados
        binding.featuredRecyclerView.apply {
            adapter = ProductAdapter(featuredProducts) { product ->
                navigateToProductDetail(product.id)
            }
        }

        // Configurar RecyclerView del catálogo
        binding.catalogRecyclerView.apply {
            adapter = ProductAdapter(catalogProducts) { product ->
                navigateToProductDetail(product.id)
            }
        }
    }

    private fun navigateToProductDetail(productId: Int) {
        val action = HomeFragmentDirections
            .actionNavigationHomeToProductDetail(productId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}