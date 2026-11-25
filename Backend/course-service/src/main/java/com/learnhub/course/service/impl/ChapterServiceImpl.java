package com.learnhub.course.service.impl;

import com.learnhub.course.dto.*;
import com.learnhub.course.exception.ResourceNotFoundException;
import com.learnhub.course.model.Chapter;
import com.learnhub.course.model.Course;
import com.learnhub.course.repository.ChapterRepository;
import com.learnhub.course.repository.CourseRepository;
import com.learnhub.course.service.ChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getChaptersByCourse(Integer courseId) {
        return chapterRepository.findByCourse_IdOrderByPositionAsc(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChapterResponseDTO createChapter(ChapterRequestDTO request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'id: " + request.getCourseId()));

        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .position(request.getPosition())
                .course(course)
                .build();

        Chapter saved = chapterRepository.save(chapter);
        log.info("Chapitre créé : id={}, courseId={}", saved.getId(), request.getCourseId());
        return mapToDTO(saved);
    }

    @Override
    public ChapterResponseDTO updateChapter(Integer id, ChapterRequestDTO request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable avec l'id: " + id));
        chapter.setTitle(request.getTitle());
        chapter.setPosition(request.getPosition());
        return mapToDTO(chapterRepository.save(chapter));
    }

    @Override
    public void deleteChapter(Integer id) {
        if (!chapterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Chapitre introuvable avec l'id: " + id);
        }
        chapterRepository.deleteById(id);
        log.info("Chapitre supprimé : id={}", id);
    }

    private ChapterResponseDTO mapToDTO(Chapter chapter) {
        List<LessonResponseDTO> lessonDTOs = chapter.getLessons().stream()
                .map(lesson -> LessonResponseDTO.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .type(lesson.getType())
                        .contentUrl(lesson.getContentUrl())
                        .durationMin(lesson.getDurationMin())
                        .position(lesson.getPosition())
                        .isFreePreview(lesson.getIsFreePreview())
                        .chapterId(chapter.getId())
                        .build())
                .collect(Collectors.toList());

        return ChapterResponseDTO.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .position(chapter.getPosition())
                .courseId(chapter.getCourse() != null ? chapter.getCourse().getId() : null)
                .lessons(lessonDTOs)
                .build();
    }
}
