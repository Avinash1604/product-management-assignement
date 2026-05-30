package com.example.products.product

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "product_variants")
class ProductVariant(
    @Column(name = "external_id")
    var externalId: Long? = null,

    @Column(length = 255)
    var title: String? = null,

    @Column(length = 255)
    var sku: String? = null,

    @Column(precision = 10, scale = 2)
    var price: BigDecimal? = null,

    @Column(nullable = false)
    var available: Boolean = true,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null
}
