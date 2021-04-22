package com.github.mcruzdev.plan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.github.mcruzdev.plan.PaginationSupport.Data.of;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;


@RestController
@RequestMapping("/plans")
public class PlanController {

    private final Logger logger = LoggerFactory.getLogger(PlanController.class);
    private final PlanRepository planRepository;

    public PlanController(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> create(@RequestBody @Valid PlanDTO planDTO) {
        return Mono.just(planDTO.convert())
                .flatMap(this.planRepository::save)
                .map(plan -> ResponseEntity.created(URI.create("/plans/" + plan.getId())).build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntityModel<Plan>>> findById(@PathVariable String id) {
        var link = linkTo(methodOn(this.getClass()).findById(id)).withSelfRel();
        return this.planRepository.findById(UUID.fromString(id))
                .map(plan -> ResponseEntity.ok(EntityModel.of(plan, link.toMono().block())));
    }

    @GetMapping
    public Mono<ResponseEntity<CollectionModel<Plan>>> find(@RequestParam(value = "description", required = false, defaultValue = "") String description,
                                                            @RequestParam(value = "status", required = false, defaultValue = "") String status,
                                                            @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        var controller = methodOn(PlanController.class);
        PageRequest pageRequest = PageRequest.of(page, size);
        Mono<Long> monoCount;
        Mono<List<Plan>> monoPlans;

        if (description.isBlank() && name.isBlank() && status.isBlank()) {
            monoPlans = this.planRepository.findByIdNotNull(pageRequest).collectList();
            monoCount = this.planRepository.count();
        } else {
            monoPlans = this.planRepository
                    .findByNameOrDescriptionOrStatus(name, description, status, PageRequest.of(page, size))
                    .collectList();
            monoCount = this.planRepository.countByNameOrDescriptionOrStatus(name, description, status);
        }

        return Mono.zip(monoCount, monoPlans)
                .flatMap(tuple -> {
                    Long count = tuple.getT1();
                    List<Plan> plans = tuple.getT2();

                    var paginationSupport = new PaginationSupport(of("/plans/", count.intValue(), size, page));

                    Mono<Link> self = linkTo(controller.find(description, status, name, page, size))
                            .withSelfRel()
                            .toMono();

                    Mono<Link> next = linkTo(controller.find(description, status, name, page + 1, size))
                            .withRel("next")
                            .toMono();

                    return Mono.zip(self, next)
                            .map(tuple2 -> ResponseEntity.ok()
                                    .header(HttpHeaders.LINK, paginationSupport.getLinkHeader())
                                    .header("X-Total-Count", count.toString())
                                    .body(CollectionModel.of(plans, tuple2.getT1(), tuple2.getT2())));

                });
//
    }

}
