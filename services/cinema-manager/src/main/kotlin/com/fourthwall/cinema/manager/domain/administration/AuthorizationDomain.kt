package com.fourthwall.cinema.manager.domain.administration

import arrow.core.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@JvmInline @Serializable value class AuthorizationToken(val token: String)

class AuthorizationDomain {
    private val adminPassword: String

    init {
        val pass = System.getenv("ADMIN_PASSWORD").toOption().getOrElse { "admin" }
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pass.toByteArray(Charsets.UTF_8))
        adminPassword = hashBytes.joinToString("") { String.format("%02x", it) }
    }

    private var authorizedTokens: Set<String> = hashSetOf()

    fun login(
        loginId: String,
        passwordHash: String
    ): Either<String, AuthorizationToken> {

        return if (loginId == "admin" && passwordHash == adminPassword) {
            authorizedTokens = authorizedTokens.plus("123")
            AuthorizationToken("123").right()
        } else {
            "Wrong login or password".left()
        }
    }

    fun logout(token: AuthorizationToken) {
        authorizedTokens = authorizedTokens.minus(token.token)
    }

    fun hasAuthorization(token: AuthorizationToken): Boolean {
        return authorizedTokens.contains(token.token)
    }

}