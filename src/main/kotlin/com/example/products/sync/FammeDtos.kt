package com.example.products.sync

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

// Only the handful of fields we care about are mapped; everything else is ignored.

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeResponse(
    val products: List<FammeProduct> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeProduct(
    val id: Long,
    val title: String,
    val handle: String? = null,
    val vendor: String? = null,
    @param:JsonProperty("product_type") val productType: String? = null,
    val variants: List<FammeVariant> = emptyList(),
    val images: List<FammeImage> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeVariant(
    val id: Long,
    val title: String? = null,
    val sku: String? = null,
    val price: BigDecimal? = null,
    val available: Boolean = true,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeImage(
    val src: String? = null,
)
