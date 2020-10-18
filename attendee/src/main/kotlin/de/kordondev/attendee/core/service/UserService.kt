package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.mail.MailSenderService
import de.kordondev.attendee.core.model.Attendee
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.model.User
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.SecurityConstants.USER_ID_PREFIX
import de.kordondev.attendee.exception.NotFoundException
import de.kordondev.attendee.exception.ResourceAlreadyExistsException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService (
        private val userRepository: UserRepository,
        private val authorityService: AuthorityService,
        private val mailSenderService: MailSenderService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
    ) {

    fun getMe(): User {
        val userIdWithPrefix = authorityService.getUserId();
        if (userIdWithPrefix.isPresent) {
            val userId = userIdWithPrefix.get().replace(USER_ID_PREFIX,"")
            return userRepository.findByIdOrNull(userId.toLong())
                    ?.let { it.copy(passWord = "") }
                    ?.let{ UserEntry.to(it) }
                ?: throw NotFoundException("user with id ${userId} not found")
        } else {
            throw NotFoundException("No userId in token")
        }
    }

    fun createUser(user: NewUser) : User {
        authorityService.isAdmin()
        userRepository
                .findOneByUserName(user.userName)
                ?.let { throw ResourceAlreadyExistsException("Username already exists") }
        val userWithEncryptedPassword = user.copy(passWord = bCryptPasswordEncoder.encode(user.passWord))
        return userRepository
                .save(UserEntry.of(userWithEncryptedPassword))
                .let { it.copy(passWord = "") }
                .let { savedUser -> UserEntry.to(savedUser) }
                .also { sendEmail(it)}
    }

    fun saveUpdatePassword(userToChange: User): User {
        authorityService.hasAuthority(userToChange, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return userRepository.findByIdOrNull(userToChange.id)
                ?.let { it.copy(passWord = bCryptPasswordEncoder.encode(userToChange.passWord)) }
                ?.let { userRepository.save(it) }
                ?.let { it.copy(passWord = "") }
                ?.let{ UserEntry.to(it)}
            ?: throw NotFoundException("user with id ${userToChange.id} not found")
    }

    fun sendEmail(user: User) {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        mailSenderService.sendRegistrationMail(
                to = user.department.leaderEMail,
                leaderName = user.department.leaderName,
                username = user.userName,
                password = user.passWord
        )
    }
}