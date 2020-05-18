package org.engine.warehouse.service;

import org.engine.warehouse.entity.UserStories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoriesRepository extends JpaRepository<UserStories, Integer>, JpaSpecificationExecutor<UserStories> {
}
