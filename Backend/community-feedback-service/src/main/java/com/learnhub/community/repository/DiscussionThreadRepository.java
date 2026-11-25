package com.learnhub.community.repository;

import com.learnhub.community.model.DiscussionThread;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, String> {
    List<DiscussionThread> findByCourseId(String courseId);
}
