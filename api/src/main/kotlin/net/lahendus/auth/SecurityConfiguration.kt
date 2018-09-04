package net.lahendus.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Autowired
    private lateinit var jwtManager: JWTManager

    override fun configure(http: HttpSecurity) {
        // TODO: try with in memory user and password
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilter(AuthorizationFilter(authenticationManager(), jwtManager))
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


class AuthorizationFilter(authenticationManager: AuthenticationManager?, private val jwtManager: JWTManager) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?) {
        jwtManager.checkValidJWTRequest(request, response)
        chain!!.doFilter(request, response)
    }
}