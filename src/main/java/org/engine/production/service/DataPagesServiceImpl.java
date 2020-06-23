package org.engine.production.service;

import org.engine.production.entity.DataPages;
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
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("dataPagesService")
@Transactional
public class DataPagesServiceImpl implements DataPagesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataPagesRepository dao;


    @Override
    public Optional<DataPages> findByLogin(String login) {
        String hql = "select e from " + DataPages.class.getName() + " e where e.login = :login";
        TypedQuery<DataPages> query = entityManager.createQuery(hql, DataPages.class).setParameter("login", login);
        List<DataPages> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<DataPages> findByEmail(String email) {
        String hql = "select e from " + DataPages.class.getName() + " e where e.email = :email";
        TypedQuery<DataPages> query = entityManager.createQuery(hql, DataPages.class).setParameter("email", email);
        List<DataPages> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<DataPages> findByConfirmationToken(String reset_password_token) {
        String hql = "select e from " + DataPages.class.getName() + " e where e.resetPasswordToken = :token";
        TypedQuery<DataPages> query = entityManager.createQuery(hql, DataPages.class).setParameter("token", reset_password_token);
        List<DataPages> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<DataPages> findByResetPasswordToken(String reset_password_token) {
        String hql = "select e from " + DataPages.class.getName() + " e where e.resetPasswordToken = :token";
        TypedQuery<DataPages> query = entityManager.createQuery(hql, DataPages.class).setParameter("token", reset_password_token);
        List<DataPages> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<DataPages> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<DataPages> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<DataPages> findAll() {
        return null;
    }

    @Override
    public Iterable<DataPages> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public void save(DataPages entity) {
        dao.save(entity);
    }

    @Override
    public Page<DataPages> findAll(int page, int size) {
        return dao.findAllByTypeIn(PageRequest.of(page, size), "AdminUser");
    }

    @Override
    public Page<DataPages> getAllBySpecification(Specification<DataPages> specification, Pageable pageable) {
        return null;
    }
}
