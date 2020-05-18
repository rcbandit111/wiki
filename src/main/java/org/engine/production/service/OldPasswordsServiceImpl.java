package org.engine.production.service;

import org.engine.production.entity.OldPasswords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
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
@Qualifier("oldPasswordsService")
@Transactional
public class OldPasswordsServiceImpl implements OldPasswordsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OldPasswordsRepository dao;

    @Override
    public Optional<OldPasswords> findEncryptedPassword(String encryptedPassword) {
        String hql = "select e from " + OldPasswords.class.getName() + " e where e.encryptedPassword = :encryptedPassword";
        TypedQuery<OldPasswords> query = entityManager.createQuery(hql, OldPasswords.class).setParameter("encryptedPassword", encryptedPassword);
        List<OldPasswords> oldPasswords = query.getResultList();
        return oldPasswords.isEmpty() ? Optional.empty() : Optional.of(oldPasswords.get(0));
    }

    @Override
    public List<OldPasswords> findByOwnerId(Integer ownerId) {
        String hql = "select e from " + OldPasswords.class.getName() + " e where e.passwordOwnerId = :passwordOwnerId ORDER BY e.createdAt DESC";
        TypedQuery<OldPasswords> query = entityManager.createQuery(hql, OldPasswords.class).setMaxResults(3).setParameter("passwordOwnerId", ownerId);
        List<OldPasswords> list = query.getResultList();
        return list;
    }

    @Override
    public Optional<OldPasswords> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<OldPasswords> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<OldPasswords> findAll() {
        return null;
    }

    @Override
    public Iterable<OldPasswords> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public void save(OldPasswords entity) {

    }

    @Override
    public Page<OldPasswords> getAllBySpecification(Specification<OldPasswords> specification, Pageable pageable) {
        return null;
    }
}
