package com.recsoft.data.repository;

import com.recsoft.data.entity.Language;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Api(value = "Репозиторий языка",
        description = "Репозиторий для работы с базой языков пользователя.")
@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findFirstByReadbleName(String name);

    @Query(value = "select l.readable_name from language l", nativeQuery = true)
    List<String> getAllByReadbleName();
}
