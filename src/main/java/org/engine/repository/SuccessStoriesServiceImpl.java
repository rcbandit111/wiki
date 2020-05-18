package org.engine.repository;

import org.engine.entity.warehouse.SuccessStories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("successStoriesService")
@Transactional
public class SuccessStoriesServiceImpl implements SuccessStoriesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SuccessStoriesRepository dao;


    @Override
    public Optional<SuccessStories> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<SuccessStories> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<SuccessStories> findAll() {
        return null;
    }

    @Override
    public Iterable<SuccessStories> findAll(List<Integer> ids) {
        return null;
    }

    @Override
    public void save(SuccessStories entity) {

    }

    @Override
    public Page<SuccessStories> getAllBySpecification(Specification<SuccessStories> specification, Pageable pageable) {
        return null;
    }
}
