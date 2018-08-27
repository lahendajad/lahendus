package net.lahendus.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JWTManager {

    @Autowired
    private lateinit var subjectWhitelist: SubjectWhitelist

    companion object {
        private const val validDuration: Long = 24
    }

    fun setNewJWT(response: HttpServletResponse, subject: String): Unit {
        subjectWhitelist.addSubject(subject)

        val jwt = Jwts.builder()
                .setExpiration(Date.from(LocalDateTime.now().plusHours(validDuration).atZone(ZoneId.systemDefault()).toInstant()))
                .setSubject(subject)
                .claim("roles", "ROLE_ADMIN,ROLE_USER")
                .signWith(SignatureAlgorithm.HS512, JWTController.KEY)
                .compact()

        response.addCookie(Cookie("JWT_SIGNATURE", jwt.split(".")[2]))
        response.setHeader("JWT-Header-Payload", jwt.split(".")[0] + "." + jwt.split(".")[1])
    }


    fun checkValidJWTRequest(request: HttpServletRequest, response: HttpServletResponse) {
        // Get cookie header, where JWT. Cookie name: ...   - Signature
        val signatureCookie: Cookie = WebUtils.getCookie(request, "JWT_SIGNATURE") ?: return

        // Get user info from JS token from  Header + payload
        val payload: String = request.getHeader("JWT-Header-Payload") ?: return

        // Concatenate 2 and 1 with '.'
        val jwt = payload + "." + signatureCookie.value

        // Validate JWT
        val parsedJWT: Jws<Claims> = Jwts.parser().setSigningKey(JWTController.KEY).parseClaimsJws(jwt)

        // Check that JWT is not revoked
        if (!subjectWhitelist.subjectIsAllowed(parsedJWT.body.subject)) return

        // Update JWT every hour
        if (parsedJWT.body.expiration.before(Date.from(LocalDateTime.now()
                        .plusHours(validDuration - 1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                ))) {
            setNewJWT(response, parsedJWT.body.subject)
        }

        // Extract roles
        val roles = parsedJWT.body.get("roles", String::class.java).split(",").map(::SimpleGrantedAuthority)
        val lahendusUser = LahendusUser(parsedJWT.body.subject)

        // Create authentication object
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(lahendusUser, null, roles)
    }
}