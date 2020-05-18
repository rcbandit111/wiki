package org.engine.warehouse.service;

import org.engine.warehouse.entity.UserStories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("userStoriesService")
@Transactional
public class UserStoriesServiceImpl implements UserStoriesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserStoriesRepository dao;


    @Override
    public Optional<UserStories> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<UserStories> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<UserStories> findAll() {
        return null;
    }

    @Override
    public Iterable<UserStories> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public void save(UserStories entity) {

    }

    @Override
    public Page<UserStories> getAllBySpecification(Specification<UserStories> specification, Pageable pageable) {
        return null;
    }
}
