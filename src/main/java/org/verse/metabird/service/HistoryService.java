package org.verse.metabird.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.verse.metabird.cache.CrudOperations;
import org.verse.metabird.model.History;
import org.verse.metabird.repository.HistoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HistoryService
        implements CrudOperations<String, History, String> {

    private final HistoryRepository repository;

    @Override
    public Mono<Boolean> create(String org, History value) {
        value.setOrg(org);

        return repository.save(value)
                .map(saved -> true)
                .onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> update(String org, History value) {
        return repository.findById(value.getId())
                .filter(existing -> existing.getOrg().equals(org))
                .flatMap(existing -> repository.save(value))
                .map(updated -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> delete(String org, String id) {
        return repository.findById(id)
                .filter(history -> history.getOrg().equals(org))
                .flatMap(repository::delete)
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<History> read(String org, String id) {
        return repository.findById(id)
                .filter(history -> history.getOrg().equals(org));
    }

    @Override
    public Flux<History> getAll(String email) {
        return repository.findByEmail(email);
    }
}
