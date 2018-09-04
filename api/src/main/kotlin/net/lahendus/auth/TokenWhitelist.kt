package net.lahendus.auth

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentSkipListSet

@Service
class TokenWhitelist {
    private val tokens: MutableSet<String> = ConcurrentSkipListSet()

    fun tokenIsAllowed(token: String): Boolean = tokens.contains(token)

    fun addToken(token: String): Boolean = tokens.add(token)

    fun removeToken(token: String): Boolean = tokens.remove(token)

    fun removeAll(): Unit = tokens.clear()
}