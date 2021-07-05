package de.kordondev.attendee.core.security

import de.kordondev.attendee.core.persistence.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class SpringSecurityConfig(
        private val userDetailsService: UserDetailsServiceImpl,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val userRepository: UserRepository
) : WebSecurityConfigurerAdapter()  {

    // Secure the endpoins with HTTP Basic authentication
    override fun configure(http: HttpSecurity) {

        http
                //HTTP Basic authentication
                .authorizeRequests()
                .antMatchers("/attendees/**").authenticated()
                .antMatchers("/departments/**").authenticated()
                .antMatchers("/users/**").authenticated()
                .antMatchers(("/login")).permitAll()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager(), userRepository))
                .addFilter(JWTAuthorizationFilter(authenticationManager(), userRepository))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors()
                .and()
                .formLogin().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowCredentials = true
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        configuration.maxAge = 1800L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


}