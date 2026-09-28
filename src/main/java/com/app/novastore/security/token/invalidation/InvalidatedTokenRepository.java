package com.app.novastore.security.token.invalidation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends CrudRepository<InvalidatedToken, String> {
}
