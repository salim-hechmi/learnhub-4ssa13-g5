package com.learnhub.course.service.impl;

import com.learnhub.course.dto.*;
import com.learnhub.course.exception.ResourceNotFoundException;
import com.learnhub.course.model.Chapter;
import com.learnhub.course.model.Lesson;
import com.learnhub.course.repository.ChapterRepository;
import com.learnhub.course.repository.LessonRepository;
import com.learnhub.course.service.LessonService;
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
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponseDTO> getLessonsByChapter(Integer chapterId) {
        return lessonRepository.findByChapter_IdOrderByPositionAsc(chapterId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponseDTO createLesson(LessonRequestDTO request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable avec l'id: " + request.getChapterId()));

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .type(request.getType())
                .contentUrl(request.getContentUrl())
                .durationMin(request.getDurationMin() != null ? request.getDurationMin() : 0)
                .position(request.getPosition())
                .isFreePreview(request.getIsFreePreview() != null ? request.getIsFreePreview() : false)
                .chapter(chapter)
                .build();

        Lesson saved = lessonRepository.save(lesson);
        log.info("Leçon créée : id={}, chapterId={}", saved.getId(), request.getChapterId());
        return mapToDTO(saved);
    }

    @Override
    public LessonResponseDTO updateLesson(Integer id, LessonRequestDTO request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leçon introuvable avec l'id: " + id));
        lesson.setTitle(request.getTitle());
        if (request.getType() != null) lesson.setType(request.getType());
        lesson.setContentUrl(request.getContentUrl());
        if (request.getDurationMin() != null) lesson.setDurationMin(request.getDurationMin());
        if (request.getPosition() != null) lesson.setPosition(request.getPosition());
        if (request.getIsFreePreview() != null) lesson.setIsFreePreview(request.getIsFreePreview());
        return mapToDTO(lessonRepository.save(lesson));
    }

    @Override
    public void deleteLesson(Integer id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leçon introuvable avec l'id: " + id);
        }
        lessonRepository.deleteById(id);
        log.info("Leçon supprimée : id={}", id);
    }

    private LessonResponseDTO mapToDTO(Lesson lesson) {
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
