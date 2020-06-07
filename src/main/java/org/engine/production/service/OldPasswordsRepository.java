package org.engine.production.service;
//package org.engine.production.repository; rename


import org.engine.production.entity.OldPasswords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OldPasswordsRepository extends JpaRepository<OldPasswords, Integer>, JpaSpecificationExecutor<OldPasswords> {
}
