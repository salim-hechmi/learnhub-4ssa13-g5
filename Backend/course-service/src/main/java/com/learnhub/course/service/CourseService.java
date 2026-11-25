package com.learnhub.course.service;

import com.learnhub.course.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/** Interface du service métier pour les cours. */
public interface CourseService {

    Page<CourseResponseDTO> getAllPublishedCourses(Integer categoryId, String level, String keyword, Pageable pageable);

    CourseResponseDTO createCourse(CourseRequestDTO request);

    CourseResponseDTO getCourseById(Integer id);

    CourseResponseDTO updateCourse(Integer id, CourseRequestDTO request);

    void deleteCourse(Integer id);

    CourseResponseDTO publishCourse(Integer id);

    CourseCurriculumDTO getCourseCurriculum(Integer id);

    CoursePricingDTO getCoursePricing(Integer id);

    List<CourseResponseDTO> getCoursesByInstructor(String instructorId);
}
