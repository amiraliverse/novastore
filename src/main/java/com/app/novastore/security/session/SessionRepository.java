package com.app.novastore.security.session;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {
    Optional<Session> findByUserIdAndIp(String userId, String ip);
    List<Session> findAllByUserId(String userId);
}
