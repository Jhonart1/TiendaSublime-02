package com.sargames.tiendasublime.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sargames.tiendasublime.models.CartItem
import com.sargames.tiendasublime.models.Product
import com.sargames.tiendasublime.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TiendaSublime.db"
        private const val DATABASE_VERSION = 4
        private const val TABLE_USERS = "users"
        private const val TABLE_PRODUCTS = "products"
        private const val TABLE_CART = "cart"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PRODUCT_NAME = "name"
        private const val COLUMN_PRODUCT_PRICE = "price"
        private const val COLUMN_PRODUCT_IMAGE = "image_url"
        private const val COLUMN_PRODUCT_CATEGORY = "category"
        private const val COLUMN_PRODUCT_IS_OFFER = "is_offer"
        private const val COLUMN_PRODUCT_IS_FEATURED = "is_featured"
        private const val COLUMN_CART_EMAIL = "email"
        private const val COLUMN_CART_PRODUCT_ID = "product_id"
        private const val COLUMN_CART_QUANTITY = "quantity"
        private const val TAG = "DatabaseHelper"
        const val ADMIN_EMAIL = "admin@admin.com"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de usuarios
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // Crear tabla de productos
        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                $COLUMN_PRODUCT_IMAGE TEXT NOT NULL,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_IS_OFFER INTEGER DEFAULT 0,
                $COLUMN_PRODUCT_IS_FEATURED INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createProductsTable)

        // Crear tabla del carrito
        val createCartTable = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_EMAIL TEXT NOT NULL,
                $COLUMN_CART_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_CART_QUANTITY INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY ($COLUMN_CART_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_ID),
                UNIQUE($COLUMN_CART_EMAIL, $COLUMN_CART_PRODUCT_ID)
            )
        """.trimIndent()
        db.execSQL(createCartTable)

        // Insertar productos iniciales
        insertInitialProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Crear tabla de productos
            val createProductsTable = """
                CREATE TABLE $TABLE_PRODUCTS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                    $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                    $COLUMN_PRODUCT_IMAGE TEXT NOT NULL,
                    $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                    $COLUMN_PRODUCT_IS_OFFER INTEGER DEFAULT 0,
                    $COLUMN_PRODUCT_IS_FEATURED INTEGER DEFAULT 0
                )
            """.trimIndent()
            db.execSQL(createProductsTable)
            insertInitialProducts(db)
        }
        
        if (oldVersion < 3) {
            // Crear tabla del carrito
            val createCartTable = """
                CREATE TABLE $TABLE_CART (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_CART_EMAIL TEXT NOT NULL,
                    $COLUMN_CART_PRODUCT_ID INTEGER NOT NULL,
                    $COLUMN_CART_QUANTITY INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY ($COLUMN_CART_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_ID),
                    UNIQUE($COLUMN_CART_EMAIL, $COLUMN_CART_PRODUCT_ID)
                )
            """.trimIndent()
            db.execSQL(createCartTable)
        }

        if (oldVersion < 4) {
            // Actualizar productos con nuevas categorías
            db.delete(TABLE_PRODUCTS, null, null)
            insertInitialProducts(db)
        }
    }

    private fun insertInitialProducts(db: SQLiteDatabase) {
        val products = listOf(
            Product(1, "Mug Clásico", "Mug de cerámica clásico con diseño minimalista", 15000.0, "https://example.com/mug_clasico.jpg", "Clásico", true, false),
            Product(2, "Mug Vintage", "Mug con diseño retro y acabado envejecido", 18000.0, "https://example.com/mug_vintage.jpg", "Vintage", true, false),
            Product(3, "Mug Moderno", "Mug con diseño contemporáneo y colores vibrantes", 20000.0, "https://example.com/mug_moderno.jpg", "Moderno", false, true),
            Product(4, "Mug Artístico", "Mug con diseños artísticos y patrones únicos", 22000.0, "https://example.com/mug_artistico.jpg", "Artístico", true, false),
            Product(5, "Mug Geek", "Mug con referencias a la cultura pop y gaming", 25000.0, "https://example.com/mug_geek.jpg", "Geek", false, true),
            Product(6, "Mug Minimalista", "Mug con diseño limpio y elegante", 17000.0, "https://example.com/mug_minimalista.jpg", "Minimalista", false, true),
            Product(7, "Mug Bohemio", "Mug con diseños étnicos y coloridos", 19000.0, "https://example.com/mug_bohemio.jpg", "Bohemio", true, false),
            Product(8, "Mug Industrial", "Mug con acabado metálico y diseño rudo", 23000.0, "https://example.com/mug_industrial.jpg", "Industrial", false, true),
            Product(9, "Mug Naturaleza", "Mug con motivos de plantas y animales", 21000.0, "https://example.com/mug_naturaleza.jpg", "Naturaleza", true, false),
            Product(10, "Mug Personalizado", "Mug con espacio para personalización", 28000.0, "https://example.com/mug_personalizado.jpg", "Personalizado", false, true)
        )

        products.forEach { product ->
            val values = ContentValues().apply {
                put(COLUMN_PRODUCT_NAME, product.name)
                put(COLUMN_PRODUCT_PRICE, product.price)
                put(COLUMN_PRODUCT_IMAGE, product.imageUrl)
                put(COLUMN_PRODUCT_CATEGORY, product.category)
                put(COLUMN_PRODUCT_IS_OFFER, if (product.isOffer) 1 else 0)
                put(COLUMN_PRODUCT_IS_FEATURED, if (product.isFeatured) 1 else 0)
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }

    fun isAdmin(email: String): Boolean {
        return email == ADMIN_EMAIL
    }

    // Métodos CRUD para productos
    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, product.name)
            put(COLUMN_PRODUCT_PRICE, product.price)
            put(COLUMN_PRODUCT_IMAGE, product.imageUrl)
            put(COLUMN_PRODUCT_CATEGORY, product.category)
            put(COLUMN_PRODUCT_IS_OFFER, if (product.isOffer) 1 else 0)
            put(COLUMN_PRODUCT_IS_FEATURED, if (product.isFeatured) 1 else 0)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    fun updateProduct(product: Product): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, product.name)
            put(COLUMN_PRODUCT_PRICE, product.price)
            put(COLUMN_PRODUCT_IMAGE, product.imageUrl)
            put(COLUMN_PRODUCT_CATEGORY, product.category)
            put(COLUMN_PRODUCT_IS_OFFER, if (product.isOffer) 1 else 0)
            put(COLUMN_PRODUCT_IS_FEATURED, if (product.isFeatured) 1 else 0)
        }
        return db.update(
            TABLE_PRODUCTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(product.id.toString())
        )
    }

    fun deleteProduct(productId: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_PRODUCTS,
            "$COLUMN_ID = ?",
            arrayOf(productId.toString())
        )
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun registerUser(name: String, email: String, password: String): Long {
        if (isEmailExists(email)) {
            Log.d(TAG, "Intento de registro con email duplicado: $email")
            return -2 // Código especial para indicar email duplicado
        }

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        logDatabaseContents() // Mostrar datos después de registrar
        return result
    }

    fun loginUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        if (exists) {
            logDatabaseContents() // Mostrar datos después de login exitoso
        }
        return exists
    }

    fun getUserName(email: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_NAME),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }

    fun logDatabaseContents() {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        Log.d(TAG, "=== Contenido de la Base de Datos ===")
        if (cursor.count == 0) {
            Log.d(TAG, "La base de datos está vacía")
        } else {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                
                Log.d(TAG, """
                    Usuario:
                    ID: $id
                    Nombre: $name
                    Email: $email
                    Contraseña: $password
                    ----------------------
                """.trimIndent())
            }
        }
        cursor.close()
    }

    // Nuevos métodos para productos
    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE))
            val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY))
            val isOffer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_OFFER)) == 1
            val isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_FEATURED)) == 1

            products.add(Product(id, name, "", price, imageUrl, category, isOffer, isFeatured))
        }
        cursor.close()
        return products
    }

    fun getOffersProducts(): List<Product> {
        return getAllProducts().filter { it.isOffer }
    }

    fun getFeaturedProducts(): List<Product> {
        return getAllProducts().filter { it.isFeatured }
    }

    fun getProductsByCategory(category: String): List<Product> {
        return getAllProducts().filter { it.category == category }
    }

    fun searchProducts(query: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = this.readableDatabase
        val searchQuery = "%$query%"
        
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_PRODUCT_NAME LIKE ?",
            arrayOf(searchQuery),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE))
            val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY))
            val isOffer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_OFFER)) == 1
            val isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_FEATURED)) == 1

            products.add(Product(id, name, "", price, imageUrl, category, isOffer, isFeatured))
        }
        cursor.close()
        return products
    }

    // Métodos para el carrito
    fun addToCart(email: String, productId: Int, quantity: Int = 1): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CART_EMAIL, email)
            put(COLUMN_CART_PRODUCT_ID, productId)
            put(COLUMN_CART_QUANTITY, quantity)
        }

        return try {
            db.insertWithOnConflict(TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir al carrito: ${e.message}")
            false
        }
    }

    fun removeFromCart(email: String, productId: Int): Boolean {
        val db = this.writableDatabase
        return try {
            db.delete(
                TABLE_CART,
                "$COLUMN_CART_EMAIL = ? AND $COLUMN_CART_PRODUCT_ID = ?",
                arrayOf(email, productId.toString())
            ) > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar del carrito: ${e.message}")
            false
        }
    }

    fun updateCartItemQuantity(email: String, productId: Int, quantity: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CART_QUANTITY, quantity)
        }

        return try {
            db.update(
                TABLE_CART,
                values,
                "$COLUMN_CART_EMAIL = ? AND $COLUMN_CART_PRODUCT_ID = ?",
                arrayOf(email, productId.toString())
            ) > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar cantidad: ${e.message}")
            false
        }
    }

    fun getCartItems(userEmail: String): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        val db = this.readableDatabase

        val query = """
            SELECT c.$COLUMN_CART_PRODUCT_ID, p.$COLUMN_PRODUCT_NAME, p.$COLUMN_PRODUCT_PRICE, 
                   p.$COLUMN_PRODUCT_IMAGE, c.$COLUMN_CART_QUANTITY
            FROM $TABLE_CART c
            INNER JOIN $TABLE_PRODUCTS p ON c.$COLUMN_CART_PRODUCT_ID = p.$COLUMN_ID
            WHERE c.$COLUMN_CART_EMAIL = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(userEmail)).use { cursor ->
            while (cursor.moveToNext()) {
                val productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY))

                cartItems.add(CartItem(
                    productId = productId,
                    name = name,
                    price = price,
                    imageUrl = imageUrl,
                    quantity = quantity
                ))
            }
        }

        return cartItems
    }

    fun getCartTotal(email: String): Double {
        val cartItems = getCartItems(email)
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun clearCart(email: String): Boolean {
        val db = this.writableDatabase
        return try {
            db.delete(TABLE_CART, "$COLUMN_CART_EMAIL = ?", arrayOf(email)) > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar carrito: ${e.message}")
            false
        }
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query("users", null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                users.add(User(id, email, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    fun userExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_EMAIL),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getProductById(productId: Int): Product? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val product = Product(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                description = "",
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)),
                isOffer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_OFFER)) == 1,
                isFeatured = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IS_FEATURED)) == 1
            )
            cursor.close()
            product
        } else {
            cursor.close()
            null
        }
    }

    fun addToCart(productId: Int, quantity: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CART_PRODUCT_ID, productId)
            put(COLUMN_CART_QUANTITY, quantity)
        }
        db.insert(TABLE_CART, null, values)
    }
} 