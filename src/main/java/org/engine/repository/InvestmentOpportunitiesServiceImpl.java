package org.engine.repository;

import org.engine.entity.warehouse.InvestmentOpportunities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("investmentOpportunitiesService")
@Transactional
public class InvestmentOpportunitiesServiceImpl implements InvestmentOpportunitiesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private InvestmentOpportunitiesRepository dao;


    @Override
    public Optional<InvestmentOpportunities> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<InvestmentOpportunities> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<InvestmentOpportunities> findAll() {
        return null;
    }

    @Override
    public Iterable<InvestmentOpportunities> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public void save(InvestmentOpportunities entity) {

    }

    @Override
    public Page<InvestmentOpportunities> getAllBySpecification(Specification<InvestmentOpportunities> specification, Pageable pageable) {
        return null;
    }
}
