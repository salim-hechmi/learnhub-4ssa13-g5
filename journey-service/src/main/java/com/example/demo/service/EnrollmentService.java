package com.example.demo.service;

import com.example.demo.dto.EnrollmentRequest;
import com.example.demo.dto.ProgressResponse;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.LessonProgress;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.LessonProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository repo;
    private final LessonProgressRepository lessonRepo;

    public EnrollmentService(EnrollmentRepository repo, LessonProgressRepository lessonRepo) {
        this.repo = repo;
        this.lessonRepo = lessonRepo;
    }

    private Enrollment get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscription " + id + " introuvable"));
    }

    // ---------- CRUD ----------

    @Transactional
    public Enrollment create(EnrollmentRequest r) {
        if (repo.existsByStudentIdAndCourseId(r.studentId(), r.courseId()))
            throw new ConflictException("Étudiant déjà inscrit à ce cours");
        Enrollment e = new Enrollment();
        e.setStudentId(r.studentId());
        e.setCourseId(r.courseId());
        e.setTotalLessons(r.totalLessons());
        if (r.status() != null) e.setStatus(r.status());
        return repo.save(e);
    }

    public List<Enrollment> findAll(Enrollment.Status status) {
        return status == null ? repo.findAll() : repo.findByStatus(status);
    }

    public List<Enrollment> findByStudent(Long studentId) {
        return repo.findByStudentId(studentId);
    }

    public Enrollment findById(Long id) {
        return get(id);
    }

    @Transactional
    public Enrollment update(Long id, EnrollmentRequest r) {
        Enrollment e = get(id);
        e.setTotalLessons(r.totalLessons());
        if (r.status() != null) e.setStatus(r.status());
        recompute(e);
        return repo.save(e);
    }

    @Transactional
    public void delete(Long id) {
        repo.delete(get(id));
    }

    // ---------- Fonctions métier de la slide ----------

    public ProgressResponse progress(Long id) {
        Enrollment e = get(id);
        long done = lessonRepo.countByEnrollmentIdAndCompletedTrue(id);
        return new ProgressResponse(e.getId(), done, e.getTotalLessons(),
                e.getProgressPercent(), e.getStatus());
    }

    @Transactional
    public Enrollment completeLesson(Long id, Long lessonId) {
        Enrollment e = get(id);                                           // 404 si inconnue
        if (lessonRepo.existsByEnrollmentIdAndLessonId(id, lessonId))
            throw new ConflictException("Leçon déjà validée");            // 409
        LessonProgress lp = new LessonProgress();
        lp.setEnrollment(e);
        lp.setLessonId(lessonId);
        lp.setCompleted(true);
        lp.setCompletedAt(LocalDateTime.now());
        lessonRepo.save(lp);
        recompute(e);
        return repo.save(e);                                              // 200
    }

    public void check(Long studentId, Long courseId) {
        if (!repo.existsByStudentIdAndCourseId(studentId, courseId))
            throw new NotFoundException("Aucune inscription");            // 404
    }

    // ---------- Calcul du pourcentage ----------

    private void recompute(Enrollment e) {
        long done = e.getId() == null ? 0 : lessonRepo.countByEnrollmentIdAndCompletedTrue(e.getId());
        int percent = e.getTotalLessons() == 0 ? 0 : (int) Math.min(100, done * 100 / e.getTotalLessons());
        e.setProgressPercent(percent);
        if (percent == 100) e.setStatus(Enrollment.Status.COMPLETED);
    }
}