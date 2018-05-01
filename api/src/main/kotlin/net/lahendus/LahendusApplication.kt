package net.lahendus

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class LahendusApplication

fun main(args: Array<String>) {
    SpringApplication.run(LahendusApplication::class.java, *args)
}