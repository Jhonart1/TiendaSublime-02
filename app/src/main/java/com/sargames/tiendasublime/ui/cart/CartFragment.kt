package com.sargames.tiendasublime.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.adapters.CartAdapter
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.FragmentCartBinding
import com.sargames.tiendasublime.models.CartItem
import com.sargames.tiendasublime.ui.products.ProductsFragment

class CartFragment : Fragment(), ProductsFragment.OnCartUpdateListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var cartAdapter: CartAdapter
    private var isViewCreated = false

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
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        setupUI()
        loadCartItems()
    }

    private fun setupUI() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem ->
                updateProductQuantity(cartItem)
            },
            onRemoveItem = { cartItem ->
                removeProductFromCart(cartItem)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        binding.btnProceedToPayment.setOnClickListener {
            if (sharedPrefs.isUserLoggedIn()) {
                // TODO: Implementar lógica de pago
                Toast.makeText(context, "Funcionalidad de pago en desarrollo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Debes iniciar sesión para proceder al pago", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCartItems() {
        if (!isViewCreated) return

        if (sharedPrefs.isUserLoggedIn()) {
            val userEmail = sharedPrefs.getUserEmail()
            if (userEmail != null) {
                val cartItems = dbHelper.getCartItems(userEmail)
                cartAdapter.updateCartItems(cartItems)
                updateTotal(cartItems)
            }
        } else {
            cartAdapter.updateCartItems(emptyList())
            updateTotal(emptyList())
        }
    }

    private fun updateProductQuantity(cartItem: CartItem) {
        if (!isViewCreated) return

        if (sharedPrefs.isUserLoggedIn()) {
            val userEmail = sharedPrefs.getUserEmail()
            if (userEmail != null) {
                if (dbHelper.updateCartItemQuantity(userEmail, cartItem.productId, cartItem.quantity)) {
                    loadCartItems() // Recargar items para actualizar el total
                } else {
                    Toast.makeText(context, "Error al actualizar la cantidad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeProductFromCart(cartItem: CartItem) {
        if (!isViewCreated) return

        if (sharedPrefs.isUserLoggedIn()) {
            val userEmail = sharedPrefs.getUserEmail()
            if (userEmail != null) {
                if (dbHelper.removeFromCart(userEmail, cartItem.productId)) {
                    loadCartItems() // Recargar items para actualizar el total
                    Toast.makeText(context, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotal(cartItems: List<CartItem>) {
        if (!isViewCreated) return

        val total = cartItems.sumOf { it.price * it.quantity }
        binding.tvTotal.text = "Total: $${String.format("%.2f", total)}"
    }

    override fun onCartUpdated() {
        loadCartItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        _binding = null
    }
} 