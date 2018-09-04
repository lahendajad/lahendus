package net.lahendus.auth

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentSkipListSet

@Service
class TokenWhitelist {
    private val tokens: MutableSet<String> = ConcurrentSkipListSet()

    // Every night at 03:00 clear expired tokens
    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Athens")
    fun clearExpiredTokens() {
        val toBeRemoved = mutableListOf<String>()

        tokens.forEach {
            try {
                Jwts.parser().setSigningKey(JWTController.KEY).parseClaimsJws(it)
            } catch (e: ExpiredJwtException) {
                toBeRemoved.add(it)
            }
        }

        toBeRemoved.forEach { removeToken(it) }
    }

    fun tokenIsAllowed(token: String): Boolean = tokens.contains(token)

    fun addToken(token: String): Boolean = tokens.add(token)

    fun removeToken(token: String): Boolean = tokens.remove(token)

    fun removeAll(): Unit = tokens.clear()
}