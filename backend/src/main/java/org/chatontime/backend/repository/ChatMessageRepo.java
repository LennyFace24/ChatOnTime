package org.chatontime.backend.repository;

import org.chatontime.backend.model.ChatMessageDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessageDetail, Integer> {
    List<ChatMessageDetail> findAllByOrderBySendTimeAsc();
}
