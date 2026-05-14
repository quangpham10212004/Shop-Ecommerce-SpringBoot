package com.shopecommerce.userserv.repository;

import com.shopecommerce.userserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
