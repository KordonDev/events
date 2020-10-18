package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.model.NewUser
import de.kordondev.attendee.core.security.PasswordGenerator
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.core.service.UserService
import de.kordondev.attendee.rest.model.RestUser
import de.kordondev.attendee.rest.model.request.RestUserRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class UserController (
        private val userService: UserService,
        private val departmentService: DepartmentService
) {

    @GetMapping("/users/me")
    fun getMe(): RestUser {
        return userService
                .getMe()
                .let { RestUser.of(it)}
    }

    @PostMapping("/users")
    fun addUser(@RequestBody(required = true) @Valid user: RestUserRequest): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
                .createUser(NewUser(
                        userName = user.username,
                        role = user.role,
                        department = department,
                        passWord = user.password ?: PasswordGenerator.generatePassword()
                ))
                .let { RestUser.of(it) }
    }

    @PutMapping("/users/{id}/password")
    fun changePassword(@RequestBody(required = true) @Valid user: RestUserRequest, @PathVariable("id") id: Long): RestUser {
        val department = departmentService.getDepartment(user.departmentId)
        return userService
                .saveUpdatePassword(RestUserRequest.to(user, id, department))
                .let { RestUser.of(it) }
    }

    @PostMapping("/users/{id}/sendRegistrationEmail")
    fun sendRegistrationEmail(@RequestBody(required = true) @Valid user: RestUserRequest, @PathVariable("id") id: Long) {
        val department = departmentService.getDepartment(user.departmentId)
        userService.sendEmail(RestUserRequest.to(user, id, department))
    }
}