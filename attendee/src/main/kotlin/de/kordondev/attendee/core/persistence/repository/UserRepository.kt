package de.kordondev.attendee.core.persistence.repository

import de.kordondev.attendee.core.persistence.entry.DepartmentEntry
import de.kordondev.attendee.core.persistence.entry.UserEntry
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntry, Long>  {
    fun findOneByUserName(userName: String): UserEntry?
    fun findOneByDepartment(department: DepartmentEntry): UserEntry?
}