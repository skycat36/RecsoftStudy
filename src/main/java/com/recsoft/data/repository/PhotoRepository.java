package com.recsoft.data.repository;

import com.recsoft.data.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

/*  Репозиторий для работы с хранимыми фотографиями
* @author Evgeny Popov */
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
