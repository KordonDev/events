package de.kordondev.attendee.core.service

import de.kordondev.attendee.core.mail.MailSenderService
import de.kordondev.attendee.core.model.*
import de.kordondev.attendee.core.persistence.entry.AttendeeEntry
import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.core.persistence.entry.UserEntry
import de.kordondev.attendee.core.persistence.repository.UserRepository
import de.kordondev.attendee.core.security.AuthorityService
import de.kordondev.attendee.core.security.PasswordGenerator
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
            return getUser(userId.toLong())
        } else {
            throw NotFoundException("No userId in token")
        }
    }

    fun getUser(id: Long): User {
        return userRepository.findByIdOrNull(id)
                ?.let { it.copy(passWord = "") }
                ?.let{ UserEntry.to(it) }
                ?: throw NotFoundException("user with id $id not found")
    }

    fun getUserForDepartment(department: Department) : User {
        val user = userRepository.findOneByDepartment(DepartmentEntry.of(department))
                ?.let { UserEntry.to(it) }
            ?: throw NotFoundException("user for department id ${department.id} not found")

        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        return user
    }

    fun createUser(user: NewUser) : User {
        authorityService.isSpecializedFieldDirector()
        if (user.role != Roles.USER) {
            authorityService.isAdmin()
        }
        userRepository
                .findOneByUserName(user.userName)
                ?.let { throw ResourceAlreadyExistsException("Username already exists") }
        val userWithEncryptedPassword = user.copy(passWord = bCryptPasswordEncoder.encode(user.passWord))
        return userRepository
                .save(UserEntry.of(userWithEncryptedPassword))
                .let { it.copy(passWord = "") }
                .let { savedUser -> UserEntry.to(savedUser) }
                .also { sendEmail(it, user.passWord)}
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

    fun updatePasswordAndSendEmail(user: User): User {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        val newPassword = PasswordGenerator.generatePassword()
        return saveUpdatePassword(user.copy(passWord = newPassword))
                .also { sendEmail(it, newPassword) }
    }

    private fun sendEmail(user: User, password: String) {
        authorityService.hasAuthority(user, listOf(Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR))
        mailSenderService.sendRegistrationMail(
                to = user.department.leaderEMail,
                leaderName = user.department.leaderName,
                username = user.userName,
                password = password
        )
    }
}