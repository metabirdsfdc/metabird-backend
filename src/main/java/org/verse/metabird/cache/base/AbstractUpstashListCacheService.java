package org.verse.metabird.cache.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.verse.metabird.cache.CrudOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractUpstashListCacheService<K, V, I>
        implements CrudOperations<K, V, I> {

    protected final WebClient upstashClient;
    protected final ObjectMapper objectMapper;
    protected final TypeReference<List<V>> listType;

    protected AbstractUpstashListCacheService(
            WebClient upstashClient,
            ObjectMapper objectMapper,
            TypeReference<List<V>> listType
    ) {
        this.upstashClient = upstashClient;
        this.objectMapper = objectMapper;
        this.listType = listType;
    }

    protected abstract String buildKey(K key);

    protected abstract Predicate<V> matchByIdentifier(I identifier);

    protected abstract Predicate<V> matchByValue(V value);

    @Override
    public Mono<Boolean> create(K key, V value) {
        String redisKey = buildKey(key);

        return getList(redisKey)
                .defaultIfEmpty(new ArrayList<>())
                .flatMap(list -> {
                    list.removeIf(matchByValue(value));
                    list.add(value);
                    return saveList(redisKey, list);
                });
    }

    @Override
    public Mono<Boolean> update(K key, V value) {
        return create(key, value);
    }

    @Override
    public Mono<Boolean> delete(K key, I identifier) {
        String redisKey = buildKey(key);

        return getList(redisKey)
                .defaultIfEmpty(new ArrayList<>())
                .flatMap(list -> {
                    int before = list.size();
                    list.removeIf(matchByIdentifier(identifier));
                    int after = list.size();
                    if (before == after) {
                        return Mono.just(false);
                    }
                    return saveList(redisKey, list);
                })
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.just(false);
                });
    }


    @Override
    public Mono<V> read(K key, I identifier) {
        return getList(buildKey(key))
                .flatMapMany(Flux::fromIterable)
                .filter(matchByIdentifier(identifier))
                .next();
    }

    @Override
    public Flux<V> getAll(K key) {
        return getList(buildKey(key))
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<List<V>> getList(String key) {
        return upstashClient.get()
                .uri("/get/{key}", key)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::deserialize)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.empty();
                });
    }

    private Mono<Boolean> saveList(String key, List<V> list) {
        return serialize(list)
                .flatMap(json ->
                        upstashClient.post()
                                .uri("/set/{key}/{value}", key, json)
                                .retrieve()
                                .bodyToMono(String.class)
                )
                .map(r -> true)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.just(false);
                });
    }


    private Mono<List<V>> deserialize(String json) {

        if (json == null || json.trim().equalsIgnoreCase("null")) {
            return Mono.empty();
        }

        try {
            JsonNode root = objectMapper.readTree(json);

            JsonNode resultNode = root.get("result");
            if (resultNode == null || resultNode.isNull()) {
                return Mono.empty();
            }

            String resultJson = resultNode.asText();

            if (resultJson == null || resultJson.isBlank()
                    || resultJson.equalsIgnoreCase("null")) {
                return Mono.empty();
            }

            List<V> list = objectMapper.readValue(resultJson, listType);

            if (list == null || list.isEmpty()) {
                return Mono.empty();
            }

            return Mono.just(list);

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }

    private Mono<String> serialize(List<V> list) {
        try {
            return Mono.just(objectMapper.writeValueAsString(list));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }
    }
}
