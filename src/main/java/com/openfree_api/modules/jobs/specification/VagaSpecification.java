package com.openfree_api.modules.jobs.specification;

import com.openfree_api.modules.jobs.dto.JobFilterRequest;
import com.openfree_api.modules.jobs.entity.Vaga;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VagaSpecification {

    private VagaSpecification() {
    }

    public static Specification<Vaga> filtro(
            JobFilterRequest filtro
    ) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getTitulo() != null &&
                    !filtro.getTitulo().isBlank()) {

                predicates.add(

                        builder.like(

                                builder.lower(
                                        root.get("titulo")
                                ),

                                "%" + filtro.getTitulo().toLowerCase() + "%"
                        )
                );
            }

            if (filtro.getCidade() != null &&
                    !filtro.getCidade().isBlank()) {

                predicates.add(

                        builder.equal(

                                builder.lower(
                                        root.get("cidade")
                                ),

                                filtro.getCidade().toLowerCase()
                        )
                );
            }

            if (filtro.getEstado() != null &&
                    !filtro.getEstado().isBlank()) {

                predicates.add(

                        builder.equal(

                                builder.lower(
                                        root.get("estado")
                                ),

                                filtro.getEstado().toLowerCase()
                        )
                );
            }

            if (filtro.getStatus() != null) {

                predicates.add(

                        builder.equal(
                                root.get("status"),
                                filtro.getStatus()
                        )
                );
            }

            return builder.and(
                    predicates.toArray(new Predicate[0])
            );

        };
    }

}