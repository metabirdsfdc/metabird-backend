package org.verse.metabird.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.verse.metabird.model.Client;
import reactor.core.publisher.Mono;

@Repository
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
    Mono<Client> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
