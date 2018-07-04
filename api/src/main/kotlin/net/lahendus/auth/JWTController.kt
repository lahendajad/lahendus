package net.lahendus.auth

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

data class CredentialsDTO(val username: String, val password: String)

@RestController
class JWTController {

    @GetMapping("/login")
    @CrossOrigin(origins = ["http://localhost:4200"])
    fun login(response: HttpServletResponse): String {
        response.addCookie(Cookie("JWT_SIGNATURE", "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"))
        return "{\"test\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ\"}"
    }

//    @GetMapping("/login")
//    @CrossOrigin(origins = ["http://localhost:4200"])
//    fun login(dto: CredentialsDTO, response: HttpServletResponse): String {
//        response.addCookie(Cookie("JWT_SIGNATURE", "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"))
//        return "{\"test\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ\"}"
//    }


    @Secured("ROLE_A")
    @GetMapping("/test")
    @CrossOrigin(origins = ["http://localhost:4200"])
    fun test(auth: Authentication) {
        println(auth)
        println(auth.principal)
    }

}