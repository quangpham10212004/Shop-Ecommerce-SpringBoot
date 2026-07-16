package com.aiecommerce.user.repository;

import com.aiecommerce.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User u
            where (:name is null or lower(u.username) like lower(concat('%', :name, '%')))
              and (:role is null or lower(u.role) = lower(:role))
              and (:department is null or lower(u.department) = lower(:department))
            """)
    List<User> search(@Param("name") String name,
                      @Param("role") String role,
                      @Param("department") String department);
}
