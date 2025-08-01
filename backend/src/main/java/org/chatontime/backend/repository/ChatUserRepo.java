package org.chatontime.backend.repository;

import org.chatontime.backend.model.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatUserRepo extends JpaRepository<ChatUser, Integer> {
    List<ChatUser> findByName(String name);
}
