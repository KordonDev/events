package de.kordondev.attendee.exception

import de.kordondev.attendee.rest.model.ApiError
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(ex: NotFoundException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<ApiError> {
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(ExistingDependencyException::class, ResourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleExistingDependencyException() {
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex:AccessDeniedException, request: WebRequest): ResponseEntity<ApiError> {
        return handleExceptionInternal(ex, HttpStatus.FORBIDDEN, request)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException() {}

    fun handleExceptionInternal(ex: RuntimeException, status: HttpStatus, request: WebRequest): ResponseEntity<ApiError> {
        val apiError = ApiError(status, ex.localizedMessage, ex.message)
        return ResponseEntity(apiError, HttpHeaders(), status)
        // return handleExceptionInternal(ex, apiError, HttpHeaders(), status, request)
        // return super.handleExceptionInternal(ex, body, headers, status, request)
    }


}