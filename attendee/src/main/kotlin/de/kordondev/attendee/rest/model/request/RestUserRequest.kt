package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.User
import de.kordondev.attendee.core.persistence.entry.Roles
import de.kordondev.attendee.exception.BadRequestException
import de.kordondev.attendee.rest.model.RestUser
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RestUserRequest(
        @field:Size(min = 4, max = 20, message = "username needs to be between 4 and 20 chars long")
        val username: String,
        @field:Size(min = 8, max = 20, message = "password needs to be between 8 and 20 chars long")
        val password: String?,
        @field:NotNull(message = "departmentId needs to be defined")
        val departmentId: Long,
        @field:NotNull(message = "role needs to be defined")
        val role: Roles
) {
        companion object {
                fun to(user: RestUserRequest, id: Long, department: Department): User {
                    if (user.password == null) {
                        throw BadRequestException("password missing")
                    }
                    return User(
                            id = id,
                            userName = user.username,
                            passWord = user.password,
                            department = department,
                            role = user.role
                    )
                }
        }
}

