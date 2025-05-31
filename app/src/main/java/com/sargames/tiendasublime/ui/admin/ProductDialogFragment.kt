package com.sargames.tiendasublime.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.sargames.tiendasublime.databinding.DialogProductFormBinding
import com.sargames.tiendasublime.models.Product

class ProductDialogFragment : DialogFragment() {
    private var _binding: DialogProductFormBinding? = null
    private val binding get() = _binding!!
    private var product: Product? = null
    private var onProductSavedListener: ((Product) -> Unit)? = null

    companion object {
        private val CATEGORIES = listOf(
            "Clásico",
            "Vintage",
            "Moderno",
            "Artístico",
            "Geek",
            "Minimalista",
            "Bohemio",
            "Industrial",
            "Naturaleza",
            "Personalizado"
        )

        fun newInstance(product: Product? = null): ProductDialogFragment {
            return ProductDialogFragment().apply {
                this.product = product
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProductFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryDropdown()
        setupProductData()
        setupButtons()
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, CATEGORIES)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupProductData() {
        product?.let { product ->
            binding.apply {
                etName.setText(product.name)
                etDescription.setText(product.description)
                etPrice.setText(product.price.toString())
                etImageUrl.setText(product.imageUrl)
                actCategory.setText(product.category, false)
                switchOffer.isChecked = product.isOffer
                switchFeatured.isChecked = product.isFeatured
            }
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            if (validateFields()) {
                saveProduct()
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        with(binding) {
            if (etName.text.isNullOrBlank()) {
                tilName.error = "El nombre es requerido"
                isValid = false
            } else {
                tilName.error = null
            }

            if (etDescription.text.isNullOrBlank()) {
                tilDescription.error = "La descripción es requerida"
                isValid = false
            } else {
                tilDescription.error = null
            }

            if (etPrice.text.isNullOrBlank()) {
                tilPrice.error = "El precio es requerido"
                isValid = false
            } else {
                tilPrice.error = null
            }

            if (etImageUrl.text.isNullOrBlank()) {
                tilImageUrl.error = "La URL de la imagen es requerida"
                isValid = false
            } else {
                tilImageUrl.error = null
            }

            if (actCategory.text.isNullOrBlank()) {
                tilCategory.error = "La categoría es requerida"
                isValid = false
            } else {
                tilCategory.error = null
            }
        }

        return isValid
    }

    private fun saveProduct() {
        with(binding) {
            val updatedProduct = Product(
                id = product?.id ?: 0,
                name = etName.text.toString(),
                description = etDescription.text.toString(),
                price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
                imageUrl = etImageUrl.text.toString(),
                category = actCategory.text.toString(),
                isOffer = switchOffer.isChecked,
                isFeatured = switchFeatured.isChecked
            )
            onProductSavedListener?.invoke(updatedProduct)
        }
    }

    fun setOnProductSavedListener(listener: (Product) -> Unit) {
        onProductSavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 