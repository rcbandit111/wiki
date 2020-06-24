package org.engine.production.service;

import org.engine.production.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("usersService")
@Transactional
public class UsersServiceImpl implements UsersService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UsersRepository dao;


    @Override
    public Optional<Users> findByLogin(String login) {
        String hql = "select e from " + Users.class.getName() + " e where e.login = :login";
        TypedQuery<Users> query = entityManager.createQuery(hql, Users.class).setParameter("login", login);
        List<Users> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        String hql = "select e from " + Users.class.getName() + " e where e.email = :email";
        TypedQuery<Users> query = entityManager.createQuery(hql, Users.class).setParameter("email", email);
        List<Users> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<Users> findByConfirmationToken(String reset_password_token) {
        String hql = "select e from " + Users.class.getName() + " e where e.resetPasswordToken = :token";
        TypedQuery<Users> query = entityManager.createQuery(hql, Users.class).setParameter("token", reset_password_token);
        List<Users> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<Users> findByResetPasswordToken(String reset_password_token) {
        String hql = "select e from " + Users.class.getName() + " e where e.resetPasswordToken = :token";
        TypedQuery<Users> query = entityManager.createQuery(hql, Users.class).setParameter("token", reset_password_token);
        List<Users> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<Users> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Users> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Users> findAll() {
        return null;
    }

    @Override
    public Iterable<Users> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public Users update(Users entity) {

        Users users = new Users();
        users.setId(entity.getId());
        users.setLogin(entity.getLogin());
        users.setEmail(entity.getEmail());
        users.setFirstName(entity.getFirstName());
        users.setLastName(entity.getLastName());
        users.setRole(entity.getRole());
        users.setEnabled(entity.getEnabled());
        users.setUpdatedAt(LocalDateTime.now());
        users.setOwnerId(entity.getOwnerId());

        return dao.save(entity);
    }

    @Override
    public void save(Users entity) {
        dao.save(entity);
    }

    @Override
    public Page<Users> findAll(int page, int size) {
        return dao.findAllByTypeIn(PageRequest.of(page, size), "AdminUser");
    }

    @Override
    public Page<Users> getAllBySpecification(Specification<Users> specification, Pageable pageable) {
        return null;
    }
}
