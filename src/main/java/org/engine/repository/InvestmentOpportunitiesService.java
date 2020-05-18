package org.engine.repository;

import org.engine.entity.warehouse.InvestmentOpportunities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface InvestmentOpportunitiesService {

    Optional<InvestmentOpportunities> findByName(String name);

    public Optional<InvestmentOpportunities> findById(Integer id);

    public Iterable<InvestmentOpportunities> findAll();

    public Iterable<InvestmentOpportunities> findAll(List<Integer> ids);

    void save(InvestmentOpportunities entity);

    public Page<InvestmentOpportunities> getAllBySpecification(final Specification<InvestmentOpportunities> specification, final Pageable pageable);
}
