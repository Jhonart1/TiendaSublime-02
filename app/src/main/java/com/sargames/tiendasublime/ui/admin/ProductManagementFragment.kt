package com.sargames.tiendasublime.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sargames.tiendasublime.adapters.ProductManagementAdapter
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.FragmentProductManagementBinding
import com.sargames.tiendasublime.models.Product

class ProductManagementFragment : Fragment() {
    private var _binding: FragmentProductManagementBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var adapter: ProductManagementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductManagementBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPrefs = SharedPreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAdminAccess()
        setupUI()
        loadProducts()
    }

    private fun checkAdminAccess() {
        val userEmail = sharedPrefs.getUserEmail()
        if (userEmail == null || !dbHelper.isAdmin(userEmail)) {
            Toast.makeText(context, "Acceso denegado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun setupUI() {
        adapter = ProductManagementAdapter(
            onEditClick = { product ->
                showEditProductDialog(product)
            },
            onDeleteClick = { product ->
                showDeleteConfirmationDialog(product)
            }
        )

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ProductManagementFragment.adapter
        }

        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadProducts() {
        val products = dbHelper.getAllProducts()
        adapter.submitList(products)
    }

    private fun showAddProductDialog() {
        val dialog = ProductDialogFragment()
        dialog.show(childFragmentManager, "AddProductDialog")
        dialog.setOnProductSavedListener { product ->
            val result = dbHelper.addProduct(product)
            if (result != -1L) {
                Toast.makeText(context, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
                loadProducts()
            } else {
                Toast.makeText(context, "Error al agregar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditProductDialog(product: Product) {
        val dialog = ProductDialogFragment.newInstance(product)
        dialog.show(childFragmentManager, "EditProductDialog")
        dialog.setOnProductSavedListener { updatedProduct ->
            val result = dbHelper.updateProduct(updatedProduct)
            if (result > 0) {
                Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                loadProducts()
            } else {
                Toast.makeText(context, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setPositiveButton("Eliminar") { _, _ ->
                val result = dbHelper.deleteProduct(product.id)
                if (result > 0) {
                    Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    loadProducts()
                } else {
                    Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 