package com.example.products

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ProductManagementApplication

fun main(args: Array<String>) {
    runApplication<ProductManagementApplication>(*args)
}
