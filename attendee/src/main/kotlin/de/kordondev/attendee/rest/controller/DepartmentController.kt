package de.kordondev.attendee.rest.controller

import de.kordondev.attendee.core.service.AttendeeService
import de.kordondev.attendee.core.service.DepartmentService
import de.kordondev.attendee.rest.model.RestAttendee
import de.kordondev.attendee.rest.model.RestDepartment
import de.kordondev.attendee.rest.model.request.RestDepartmentRequest
import org.springframework.web.bind.annotation.*

@RestController
class DepartmentController(
        private val departmentService: DepartmentService,
        private val attendeeService: AttendeeService
) {
    @GetMapping("/department")
    fun getDepartments(): List<RestDepartment> {
        return departmentService
                .getDepartments()
                .map{ department -> RestDepartment.of(department)}
    }

    @GetMapping("/department/{id}")
    fun getDepartment(@PathVariable(value = "id") id: Long): RestDepartment {
        return departmentService
                .getDepartment(id)
                .let{ department -> RestDepartment.of(department)}
    }

    @PostMapping("/department")
    fun addDepartment(@RequestBody(required = true) department: RestDepartmentRequest): RestDepartment {
        return departmentService
                .createDepartment(RestDepartmentRequest.to(department))
                .let { savedDepartment -> RestDepartment.of(savedDepartment) }
    }

    @PutMapping("/department/{id}")
    fun saveDepartment(@RequestBody(required = true) department: RestDepartmentRequest, @PathVariable("id") id: Long): RestDepartment {
        return departmentService
                .saveDepartment(id, RestDepartmentRequest.to(department))
                .let { savedDepartment -> RestDepartment.of(savedDepartment) }
    }

    @DeleteMapping("/department/{id}")
    fun deleteDepartment(@PathVariable("id") id: Long) {
        departmentService.deleteDepartment(id)
    }

    @GetMapping("/department/{id}/attendees")
    fun getAttendeesForDepartment(@PathVariable(value = "id") id: Long): Iterable<RestAttendee> {
        return departmentService
                .getDepartment(id)
                .let { department -> attendeeService.getAttendeesForDepartment(department) }
                .let { attendees -> attendees.map { attendee -> RestAttendee.of(attendee) } }
    }
}