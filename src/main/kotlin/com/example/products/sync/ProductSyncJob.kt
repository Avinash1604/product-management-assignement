package com.example.products.sync

import com.example.products.product.Product
import com.example.products.product.ProductRepository
import com.example.products.product.ProductVariant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class ProductSyncJob(
    private val fammeClient: FammeClient,
    private val productRepository: ProductRepository,
    @param:Value("\${famme.max-products}") private val maxProducts: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Runs immediately at startup (initialDelay = 0) and then every 6 hours.
     * Upserts products keyed by their famme.no id so re-runs don't create duplicates.
     */
    @Scheduled(initialDelay = 0, fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    @Transactional
    fun sync() {
        val fammeProducts = fammeClient.fetchProducts(maxProducts)
        log.info("Syncing {} products from famme.no", fammeProducts.size)

        for (fp in fammeProducts) {
            val product = productRepository.findByExternalId(fp.id)
                ?: Product(externalId = fp.id, title = fp.title)

            product.title = fp.title
            product.vendor = fp.vendor
            product.productType = fp.productType
            product.handle = fp.handle
            product.imageUrl = fp.images.firstOrNull()?.src

            // Replace variants wholesale; orphanRemoval deletes the old rows.
            product.variants.clear()
            for (fv in fp.variants) {
                product.addVariant(
                    ProductVariant(
                        externalId = fv.id,
                        title = fv.title,
                        sku = fv.sku,
                        price = fv.price,
                        available = fv.available,
                    ),
                )
            }

            productRepository.save(product)
        }

        log.info("Product sync complete; {} products now in the database", productRepository.count())
    }
}
