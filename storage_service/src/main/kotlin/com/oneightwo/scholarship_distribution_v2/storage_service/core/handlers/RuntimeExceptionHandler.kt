package com.oneightwo.scholarship_distribution_v2.storage_service.core.handlers

import com.oneightwo.scholarship_distribution_v2.models.ResponseBody
import com.oneightwo.scholarship_distribution_v2.storage_service.core.exceptions.ObjectNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RuntimeExceptionHandler {

//    @ExceptionHandler(NotEnoughStudentsException::class)
//    protected fun handleNotEnoughStudentsException(ex: NotEnoughStudentsException): ResponseEntity<Any?>? {
//        val exceptionView = ErrorView(
//            HttpStatus.CONFLICT,
//            "Отсутствует необходимое количество студентов для распрделения стипендий",
//            ex.getMessage()
//        )
//        return ResponseEntity(
//            exceptionView,
//            HttpStatus.CONFLICT
//        )
//    }

//    @ExceptionHandler(RegistrationIsNotActiveException::class)
//    protected fun handleRegistrationIsNotActiveException(ex: RegistrationIsNotActiveException): ResponseEntity<Any?>? {
//        val exceptionView = ErrorView(
//            HttpStatus.CONFLICT,
//            "Регистрация в данный момент не активна",
//            ex.getMessage()
//        )
//        return ResponseEntity(
//            exceptionView,
//            HttpStatus.CONFLICT
//        )
//    }

    @ExceptionHandler(ObjectNotFoundException::class)
    protected fun handleStudentNotFoundException(ex: ObjectNotFoundException): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.name,
            "Объект не найден",
            ex.message
        )
        return ResponseEntity(
            responseBody,
            HttpStatus.NOT_FOUND
        )
    }

//    @ExceptionHandler(CoreException::class)
//    protected fun handleCoreException(ex: CoreException): ResponseEntity<Any?>? {
//        val responseBody = ResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, "CoreException", ex.getMessage())
//        return ResponseEntity(
//            responseBody,
//            HttpStatus.INTERNAL_SERVER_ERROR
//        )
//    }
}