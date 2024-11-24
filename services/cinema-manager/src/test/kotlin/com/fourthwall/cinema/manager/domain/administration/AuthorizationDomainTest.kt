package com.fourthwall.cinema.manager.domain.administration

import java.security.MessageDigest
import kotlin.test.*


class AuthorizationDomainTest {

    private lateinit var authorization: AuthorizationDomain

    @BeforeTest
    fun setUp() {
        System.setProperty(
            "ADMIN_PASSWORD", "admin"
        )
        authorization = AuthorizationDomain()
    }

    @Test
    fun `test successful login`() {
        val loginResult = authorization.login("admin", hashPassword("admin"))

        assertTrue(loginResult.isRight())
        assertEquals("123", loginResult.orNull()?.token)
    }

    @Test
    fun `test failed login with wrong password`() {
        val loginResult = authorization.login("admin", "wrongPasswordHash")

        assertTrue(loginResult.isLeft())
        assertEquals("Wrong login or password", loginResult.swap().orNull())
    }

    @Test
    fun `test failed login with wrong login ID`() {
        val loginResult = authorization.login("user", hashPassword("admin"))

        assertTrue(loginResult.isLeft())
        assertEquals("Wrong login or password", loginResult.swap().orNull())
    }

    @Test
    fun `test logout`() {
        val loginResult = authorization.login("admin", hashPassword("admin"))
        val token = loginResult.orNull()

        assertNotNull(token)

        authorization.logout(token!!)

        assertFalse(authorization.hasAuthorization(token))
    }

    @Test
    fun `test hasAuthorization after login and logout`() {
        val loginResult = authorization.login("admin", hashPassword("admin"))
        val token = loginResult.orNull()

        assertNotNull(token)
        assertTrue(authorization.hasAuthorization(token!!))

        authorization.logout(token)

        assertFalse(authorization.hasAuthorization(token))
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { String.format("%02x", it) }
    }
}