package com.learnhub.community.controller;

import com.learnhub.community.exception.NotFoundException;
import com.learnhub.community.model.DiscussionThread;
import com.learnhub.community.repository.DiscussionThreadRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/threads")
public class DiscussionThreadController extends BaseCrudController<DiscussionThread> {

    private final DiscussionThreadRepository repository;

    public DiscussionThreadController(DiscussionThreadRepository repository) { this.repository = repository; }

    @Override protected JpaRepository<DiscussionThread, String> repo() { return repository; }
    @Override protected void setId(DiscussionThread e, String id) { e.setId(id); }
    @Override protected String label() { return "Fil de discussion"; }

    @GetMapping("/course/{courseId}")
    public List<DiscussionThread> byCourse(@PathVariable String courseId) {
        return repository.findByCourseId(courseId);
    }

    /** POST /api/threads/{id}/replies : ajoute une réponse imbriquée. */
    @PostMapping("/{id}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public DiscussionThread reply(@PathVariable String id, @RequestBody DiscussionThread.Reply reply) {
        DiscussionThread t = repository.findById(id).orElseThrow(() -> new NotFoundException("Fil introuvable : " + id));
        t.getReplies().add(reply);
        return repository.save(t);
    }
}
