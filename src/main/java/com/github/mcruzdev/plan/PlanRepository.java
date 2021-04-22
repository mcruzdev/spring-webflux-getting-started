package com.github.mcruzdev.plan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PlanRepository extends ReactiveCrudRepository<Plan, UUID> {

    Flux<Plan> findByNameOrDescriptionOrStatus(String name, String description, String status, Pageable pageable);

    Flux<Plan> findByIdNotNull(Pageable pageable);

    Mono<Long> countByNameOrDescriptionOrStatus(String name, String description, String status);
}
