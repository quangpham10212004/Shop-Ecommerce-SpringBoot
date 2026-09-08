package com.aiecommerce.user.service;

import com.aiecommerce.user.dto.UserRequest;
import com.aiecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserService {
    public User create(UserRequest request);
    public List<User> getAll();
    public List<User> search(String name, String role, String department);
    public User getById(String id);
    public User update(String id, UserRequest request);
    public void delete(String id);

}
