package com.sargames.tiendasublime.models

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    var quantity: Int
) 