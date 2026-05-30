package com.example.products.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findByExternalId(externalId: Long): Product?

    /** Case-insensitive substring match on title, newest first. */
    fun findByTitleContainingIgnoreCaseOrderByIdDesc(title: String): List<Product>
}
