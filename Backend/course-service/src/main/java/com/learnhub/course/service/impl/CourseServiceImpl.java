package com.learnhub.course.service.impl;

import com.learnhub.course.dto.*;
import com.learnhub.course.exception.ResourceNotFoundException;
import com.learnhub.course.messaging.CourseEventPublisher;
import com.learnhub.course.model.*;
import com.learnhub.course.repository.*;
import com.learnhub.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseEventPublisher eventPublisher;

    // =====================================================================
    // READ
    // =====================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAllPublishedCourses(Integer categoryId, String level, String keyword, Pageable pageable) {
        Page<Course> courses;

        if (keyword != null && !keyword.isBlank()) {
            courses = courseRepository.searchPublished(CourseStatus.PUBLISHED, keyword, pageable);
        } else if (categoryId != null) {
            courses = courseRepository.findByStatusAndCategory_Id(CourseStatus.PUBLISHED, categoryId, pageable);
        } else if (level != null && !level.isBlank()) {
            courses = courseRepository.findByStatusAndLevel(CourseStatus.PUBLISHED, CourseLevel.valueOf(level.toUpperCase()), pageable);
        } else {
            courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        }

        return courses.map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Integer id) {
        Course course = findCourseOrThrow(id);
        return mapToResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseCurriculumDTO getCourseCurriculum(Integer id) {
        Course course = findCourseOrThrow(id);

        List<ChapterResponseDTO> chapterDTOs = course.getChapters().stream()
                .map(chapter -> {
                    List<LessonResponseDTO> lessonDTOs = chapter.getLessons().stream()
                            .map(this::mapLessonToDTO)
                            .collect(Collectors.toList());
                    return ChapterResponseDTO.builder()
                            .id(chapter.getId())
                            .title(chapter.getTitle())
                            .position(chapter.getPosition())
                            .courseId(id)
                            .lessons(lessonDTOs)
                            .build();
                })
                .collect(Collectors.toList());

        int totalLessons = chapterDTOs.stream()
                .mapToInt(c -> c.getLessons() != null ? c.getLessons().size() : 0)
                .sum();

        int totalDuration = course.getChapters().stream()
                .flatMap(ch -> ch.getLessons().stream())
                .mapToInt(l -> l.getDurationMin() != null ? l.getDurationMin() : 0)
                .sum();

        return CourseCurriculumDTO.builder()
                .courseId(id)
                .courseTitle(course.getTitle())
                .chapters(chapterDTOs)
                .totalLessons(totalLessons)
                .totalDurationMin(totalDuration)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CoursePricingDTO getCoursePricing(Integer id) {
        Course course = findCourseOrThrow(id);
        return new CoursePricingDTO(course.getId(), course.getTitle(), course.getPrice(),
                course.getStatus() == null ? null : course.getStatus().name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByInstructor(String instructorId) {
        return courseRepository.findByInstructorId(instructorId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // WRITE
    // =====================================================================

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        log.info("Création d'un nouveau cours : title={}, instructorId={}", request.getTitle(), request.getInstructorId());

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .level(request.getLevel() != null ? request.getLevel() : CourseLevel.BEGINNER)
                .status(CourseStatus.DRAFT)
                .instructorId(request.getInstructorId())
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id: " + request.getCategoryId()));
            course.setCategory(category);
        }

        Course saved = courseRepository.save(course);
        log.info("Cours créé avec succès : id={}", saved.getId());
        return mapToResponseDTO(saved);
    }

    @Override
    public CourseResponseDTO updateCourse(Integer id, CourseRequestDTO request) {
        Course course = findCourseOrThrow(id);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getLevel() != null) course.setLevel(request.getLevel());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id: " + request.getCategoryId()));
            course.setCategory(category);
        }

        Course updated = courseRepository.save(course);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteCourse(Integer id) {
        Course course = findCourseOrThrow(id);
        courseRepository.delete(course);
        log.info("Cours supprimé : id={}", id);
    }

    @Override
    public CourseResponseDTO publishCourse(Integer id) {
        Course course = findCourseOrThrow(id);

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            log.warn("Le cours {} est déjà publié", id);
            return mapToResponseDTO(course);
        }

        course.setStatus(CourseStatus.PUBLISHED);
        Course published = courseRepository.save(course);

        // Publication de l'événement RabbitMQ "CoursePublished"
        eventPublisher.publishCoursePublished(published.getId(), published.getTitle(), published.getInstructorId());
        log.info("Cours publié et événement RabbitMQ émis : id={}", id);

        return mapToResponseDTO(published);
    }

    // =====================================================================
    // HELPERS DE MAPPING
    // =====================================================================

    private Course findCourseOrThrow(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'id: " + id));
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .level(course.getLevel())
                .status(course.getStatus())
                .instructorId(course.getInstructorId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .createdAt(course.getCreatedAt())
                .chaptersCount(course.getChapters() != null ? course.getChapters().size() : 0)
                .build();
    }

    private LessonResponseDTO mapLessonToDTO(Lesson lesson) {
        return LessonResponseDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .contentUrl(lesson.getContentUrl())
                .durationMin(lesson.getDurationMin())
                .position(lesson.getPosition())
                .isFreePreview(lesson.getIsFreePreview())
                .chapterId(lesson.getChapter() != null ? lesson.getChapter().getId() : null)
                .build();
    }
}
