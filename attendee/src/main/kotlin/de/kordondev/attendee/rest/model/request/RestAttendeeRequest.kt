package de.kordondev.attendee.rest.model.request

import de.kordondev.attendee.core.model.Department
import de.kordondev.attendee.core.model.NewAttendee
import de.kordondev.attendee.core.persistence.entry.AttendeeRole
import de.kordondev.attendee.core.persistence.entry.Food
import de.kordondev.attendee.core.persistence.entry.TShirtSize
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RestAttendeeRequest(
        @field:NotBlank(message = "firstName cannot be blank")
        val firstName: String,
        @field:NotBlank(message = "lastName cannot be blank")
        val lastName: String,
        @field:NotNull(message = "departmentId missing")
        val departmentId: Long,
        @field:NotNull(message = "birthday is missing")
        val birthday: String,
        @field:NotNull(message = "food is missing")
        val food: Food,
        @field:NotNull(message = "tShirtSize is missing")
        val tShirtSize: TShirtSize,
        val additionalInformation: String,
        @field:NotNull(message = "role is missing")
        val role: AttendeeRole
) {
    companion object {
        fun to(attendee: RestAttendeeRequest, department: Department): NewAttendee {
            return NewAttendee(
                    firstName = attendee.firstName,
                    lastName = attendee.lastName,
                    birthday = attendee.birthday,
                    food = attendee.food,
                    tShirtSize = attendee.tShirtSize,
                    additionalInformation = attendee.additionalInformation,
                    role = attendee.role,
                    department = department
            )
        }
    }
}