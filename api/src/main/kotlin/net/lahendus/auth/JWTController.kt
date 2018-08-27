package net.lahendus.auth

import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletResponse


@RestController
class JWTController {

    @Autowired
    private lateinit var jwtManager: JWTManager

    companion object {
        val KEY: SecretKey = MacProvider.generateKey()
    }

    @GetMapping("/login") //TODO: Google authentication.
    fun login(response: HttpServletResponse) {
        jwtManager.setNewJWT(response, "some@some.ee")
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/test")
    fun test(auth: Authentication?, response: HttpServletResponse) {
        println(auth?.principal)
        println("Some roles.")
    }
}