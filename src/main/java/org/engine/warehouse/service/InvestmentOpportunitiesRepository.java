package org.engine.warehouse.service;

import org.engine.warehouse.entity.InvestmentOpportunities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentOpportunitiesRepository extends JpaRepository<InvestmentOpportunities, Integer>, JpaSpecificationExecutor<InvestmentOpportunities> {
}
