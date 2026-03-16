package com.company.codelyst.repository;

import com.company.codelyst.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
