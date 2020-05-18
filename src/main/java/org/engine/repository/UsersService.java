package org.engine.repository;

import org.engine.entity.production.Users;
import org.engine.entity.warehouse.SuccessStories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Optional<Users> findByLogin(String login);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByConfirmationToken(String confirmation_token);

    Optional<Users> findByResetPasswordToken(String reset_password_token);

    Optional<Users> findByName(String name);

    public Optional<Users> findById(Integer id);

    public Iterable<Users> findAll();

    public Iterable<Users> findAll(List<Integer> ids);

    void save(Users entity);

    public Page<Users> findAll(int page, int size);

    public Page<Users> getAllBySpecification(final Specification<Users> specification, final Pageable pageable);
}
