package org.engine.production.service;

import org.engine.production.entity.DataPages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface DataPagesService {

    Optional<DataPages> findByLogin(String login);

    Optional<DataPages> findByEmail(String email);

    Optional<DataPages> findByConfirmationToken(String confirmation_token);

    Optional<DataPages> findByResetPasswordToken(String reset_password_token);

    Optional<DataPages> findByName(String name);

    public Optional<DataPages> findById(Integer id);

    public Iterable<DataPages> findAll();

    public Iterable<DataPages> findAll(List<Integer> ids);

    void save(DataPages entity);

    public Page<DataPages> findAll(int page, int size);

    public Page<DataPages> getAllBySpecification(final Specification<DataPages> specification, final Pageable pageable);
}
