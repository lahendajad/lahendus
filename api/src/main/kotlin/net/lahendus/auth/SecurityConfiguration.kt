package net.lahendus.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilter(AuthorizationFilter(authenticationManager()))
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry?) {
                registry!!.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowCredentials(true)
                        .allowedMethods("*")
                        .exposedHeaders("JWT-Header-Payload")
            }
        }
    }
}

class AuthorizationFilter(authenticationManager: AuthenticationManager?) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        check(request)
        chain!!.doFilter(request, response)
    }

    private fun check(request: HttpServletRequest?) {
        // 1. Get cookie header, where JWT. Cookie name: ...   - Signature
        val signatureCookie: Cookie = WebUtils.getCookie(request!!, "JWT_SIGNATURE") ?: return
        // 2. Get user info from JS token from  Header + payload
        val payload: String = request.getHeader("JWT-Header-Payload") ?: return
        // 3. Concatenate 2 and 1 with '.'
        val jwt = payload + "." + signatureCookie.value
        // 4. Validate JWT
        val parsedJWT: Jws<Claims> = Jwts.parser().setSigningKey(JWTController.KEY).parseClaimsJws(jwt)
        // 4.5 check that JWT is still valid and update if necessary
        // TODO: update JWT
        // 5. Check role and access against endpoint
        val roles = parsedJWT.body.get("roles", String::class.java).split(",").map(::SimpleGrantedAuthority)
        val lahendusUser = LahendusUser(parsedJWT.body.subject)
        // 6. Principal
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(lahendusUser, null, roles)
    }
}