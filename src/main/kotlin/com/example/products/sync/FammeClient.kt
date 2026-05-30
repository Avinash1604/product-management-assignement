package com.example.products.sync

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class FammeClient(
    builder: RestClient.Builder,
    @param:Value("\${famme.products-url}") private val productsUrl: String,
) {
    private val restClient = builder.build()

    /**
     * Fetches products from famme.no and returns at most [limit] of them.
     * The endpoint supports a `limit` query param (max 250), so we ask for a generous page
     * and then trim to the configured maximum.
     */
    fun fetchProducts(limit: Int): List<FammeProduct> {
        val response = restClient.get()
            .uri("$productsUrl?limit=250")
            .retrieve()
            .body<FammeResponse>()
        return response?.products.orEmpty().take(limit)
    }
}
