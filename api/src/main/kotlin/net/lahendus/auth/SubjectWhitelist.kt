package net.lahendus.auth

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentSkipListSet

@Service
class SubjectWhitelist {
    private val subjects: MutableSet<String> = ConcurrentSkipListSet()

    fun subjectIsAllowed(subject: String): Boolean = subjects.contains(subject)

    fun addSubject(subject: String): Boolean = subjects.add(subject)

    fun removeSubject(subject: String): Boolean = subjects.remove(subject)

    fun removeAll(): Unit = subjects.clear()
}