package com.oneightwo.scholarship_distribution_v2.student_service.helpers

import com.oneightwo.scholarship_distribution_v2.student_service.dto.CourseDTO
import com.oneightwo.scholarship_distribution_v2.student_service.dto.ScienceDirectionDTO
import com.oneightwo.scholarship_distribution_v2.student_service.dto.StudentDTO
import com.oneightwo.scholarship_distribution_v2.student_service.dto.UniversityDTO
import com.oneightwo.scholarship_distribution_v2.student_service.models.Course
import com.oneightwo.scholarship_distribution_v2.student_service.models.ScienceDirection
import com.oneightwo.scholarship_distribution_v2.student_service.models.Student
import com.oneightwo.scholarship_distribution_v2.student_service.models.University
import org.springframework.stereotype.Component

class TransformationHelper {

    companion object {
        fun entityToDto(course: Course) = CourseDTO(
            course.id,
            course.name
        )

        fun dtoToEntity(courseDTO: CourseDTO) = Course(
            courseDTO.id,
            courseDTO.name
        )

        fun entityToDto(university: University) = UniversityDTO(
            university.id,
            university.name,
            university.abbreviation
        )

        fun dtoToEntity(universityDTO: UniversityDTO) = University(
            universityDTO.id,
            universityDTO.name,
            universityDTO.abbreviation
        )

        fun entityToDto(scienceDirection: ScienceDirection) = ScienceDirectionDTO(
            scienceDirection.id,
            scienceDirection.name
        )

        fun dtoToEntity(scienceDirectionDTO: ScienceDirectionDTO) = ScienceDirection(
            scienceDirectionDTO.id,
            scienceDirectionDTO.name
        )

        fun dtoToEntity(studentDTO: StudentDTO) = Student(
            studentDTO.id,
            studentDTO.surname,
            studentDTO.name,
            studentDTO.patronymic,
            University(studentDTO.universityId),
            studentDTO.faculty,
            Course(studentDTO.courseId),
            studentDTO.email,
            studentDTO.phone,
            ScienceDirection(studentDTO.scienceDirectionId),
            studentDTO.topic,
            studentDTO.c1,
            studentDTO.c2,
            studentDTO.c3,
            studentDTO.c4,
            studentDTO.c5,
            studentDTO.c6,
            studentDTO.c7,
            studentDTO.c8,
            studentDTO.c9,
            studentDTO.c10,
            studentDTO.c11,
            studentDTO.c12,
            studentDTO.c13,
            studentDTO.c14,
            studentDTO.c15,
            studentDTO.rating,
            studentDTO.isValid
        )


        fun entityToDto(student: Student) = StudentDTO(
            student.id,
            student.surname,
            student.name,
            student.patronymic,
            student.university!!.id,
            student.faculty,
            student.course!!.id,
            student.email,
            student.phone,
            student.scienceDirection!!.id,
            student.topic,
            student.c1,
            student.c2,
            student.c3,
            student.c4,
            student.c5,
            student.c6,
            student.c7,
            student.c8,
            student.c9,
            student.c10,
            student.c11,
            student.c12,
            student.c13,
            student.c14,
            student.c15,
            student.rating,
            student.isValid
        )
    }
}