package org.engine.warehouse.service;

import org.engine.warehouse.entity.SuccessStories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface SuccessStoriesService {

    Optional<SuccessStories> findByName(String name);

    public Optional<SuccessStories> findById(Integer id);

    public Iterable<SuccessStories> findAll();

    public Iterable<SuccessStories> findAll(List<Integer> ids);

    void save(SuccessStories entity);

    public Page<SuccessStories> getAllBySpecification(final Specification<SuccessStories> specification, final Pageable pageable);
}
