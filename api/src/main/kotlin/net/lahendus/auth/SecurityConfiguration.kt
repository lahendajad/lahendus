package net.lahendus.auth

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
import org.springframework.web.util.WebUtils
import java.util.*
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
}

class AuthorizationFilter(authenticationManager: AuthenticationManager?) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        check(request)

        chain!!.doFilter(request, response)
    }

    private fun check(request: HttpServletRequest?) {
        // 1. Get cookie header, where JWT. Cookie name: ...   - Signature
        val signatureCookie: Cookie = WebUtils.getCookie(request!!, "JWT_SIGNATURE") ?: return
        // 2. Get user info from JS token from body -  Header + payload
        val payloadCookie: Cookie = WebUtils.getCookie(request, "JWT_HEADER_PAYLOAD") ?: return
        // 3. Concatenate 2 and 1 with '.'
        val jwt = signatureCookie.value + '.' + payloadCookie.value
        // 4. Validate JWT

        // 4.5 check that JWT is still valid

        // 5. Check role and access against endpoint
        // TODO: get actual roles and user
        val roles = Arrays.asList(SimpleGrantedAuthority("ROLE_A"))
        val lahendusUser = LahendusUser("someUser@gmail.com", "Some", "Some")
        // 6. Principal?
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(lahendusUser, null, roles)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}