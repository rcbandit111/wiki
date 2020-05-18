package org.engine.repository;

import org.engine.entity.production.OldPasswords;
import org.engine.entity.warehouse.InvestmentOpportunities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OldPasswordsRepository extends JpaRepository<OldPasswords, Integer>, JpaSpecificationExecutor<OldPasswords> {
}
