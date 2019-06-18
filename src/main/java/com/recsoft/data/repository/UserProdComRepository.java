package com.recsoft.data.repository;


import com.recsoft.data.entity.UserProdCom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProdComRepository extends JpaRepository<UserProdCom, Long> {
}
