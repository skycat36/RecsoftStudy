package com.recsoft.data.repository;

import com.recsoft.data.entity.PhotoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {
}
