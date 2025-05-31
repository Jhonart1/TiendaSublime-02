package com.sargames.tiendasublime.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isOffer: Boolean = false,
    val isFeatured: Boolean = false
) 