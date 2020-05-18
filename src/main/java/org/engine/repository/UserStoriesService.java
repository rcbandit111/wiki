package org.engine.repository;

import org.engine.entity.warehouse.UserStories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserStoriesService {

    Optional<UserStories> findByName(String name);

    public Optional<UserStories> findById(Integer id);

    public Iterable<UserStories> findAll();

    public Iterable<UserStories> findAll(List<Integer> ids);

    void save(UserStories entity);

    public Page<UserStories> getAllBySpecification(final Specification<UserStories> specification, final Pageable pageable);
}
