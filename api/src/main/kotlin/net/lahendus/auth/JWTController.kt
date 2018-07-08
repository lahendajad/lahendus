package net.lahendus.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


data class CredentialsDTO(val username: String, val password: String)


@RestController
class JWTController {

    companion object {
        val KEY: SecretKey = MacProvider.generateKey()
    }

    @GetMapping("/login")
    fun login(response: HttpServletResponse) {
        val compactJws: String = Jwts.builder()
                .setExpiration(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .setSubject("some@some.ee")
                .claim("roles", "ROLE_ADMIN,ROLE_USER")
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact()

        response.addCookie(Cookie("JWT_SIGNATURE", compactJws.split(".")[2]))
        response.setHeader("JWT-Header-Payload", compactJws.split(".")[0] + "." + compactJws.split(".")[1])
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/test")
    fun test(auth: Authentication?, response: HttpServletResponse) {
        println(auth?.principal)
        println("Some roles.")
    }
}