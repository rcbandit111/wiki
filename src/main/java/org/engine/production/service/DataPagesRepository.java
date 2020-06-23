package org.engine.production.service;

import org.engine.production.entity.DataPages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DataPagesRepository extends JpaRepository<DataPages, Integer>, JpaSpecificationExecutor<DataPages> {

    Page<DataPages> findAllByTypeIn(Pageable page, String... types);
}
