package org.engine.repository;

import org.engine.entity.production.OldPasswords;
import org.engine.entity.warehouse.InvestmentOpportunities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface OldPasswordsService {

    Optional<OldPasswords> findEncryptedPassword(String encryptedPassword);

    List<OldPasswords> findByOwnerId(Integer ownerId);

    Optional<OldPasswords> findByName(String name);

    public Optional<OldPasswords> findById(Integer id);

    public Iterable<OldPasswords> findAll();

    public Iterable<OldPasswords> findAll(List<Integer> ids);

    void save(OldPasswords entity);

    public Page<OldPasswords> getAllBySpecification(final Specification<OldPasswords> specification, final Pageable pageable);
}
