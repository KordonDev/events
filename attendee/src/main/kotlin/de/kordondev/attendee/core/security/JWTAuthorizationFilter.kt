package de.kordondev.attendee.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.core.security.SecurityConstants.HEADER_STRING
import de.kordondev.attendee.core.security.SecurityConstants.SECRET
import de.kordondev.attendee.core.security.SecurityConstants.TOKEN_PREFIX
import de.kordondev.attendee.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.attendee.core.security.SecurityConstants.DEPARTMENT_ID_PREFIX
import de.kordondev.attendee.core.security.SecurityConstants.ROLE_PREFIX
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
        authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository
) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(HEADER_STRING)

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        val authentication = getAuthentication(request);

        if (authentication != null) {
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(HEADER_STRING)
        if (token != null) {
            val username = JWT.require(Algorithm.HMAC512(SECRET))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .subject
            if (username != null) {
                return userRepository.findOneByUserName(username)
                        ?.let { UserEntry.to(it) }
                        ?.let {user ->
                             UsernamePasswordAuthenticationToken(
                                user.userName,
                                null,
                                listOf(
                                        // PREFIX ME
                                        SimpleGrantedAuthority(USER_ID_PREFIX + user.id.toString()),
                                        SimpleGrantedAuthority(DEPARTMENT_ID_PREFIX + user.department.id.toString()),
                                        SimpleGrantedAuthority(ROLE_PREFIX + user.role.toString())
                                )
                            )
                        }
            }
        }
        return null
    }
}