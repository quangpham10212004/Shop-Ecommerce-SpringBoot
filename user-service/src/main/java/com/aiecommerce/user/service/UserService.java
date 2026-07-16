package com.aiecommerce.user.service;

import com.aiecommerce.user.dto.UserRequest;
import com.aiecommerce.user.entity.User;
import com.aiecommerce.user.exception.ResourceNotFoundException;
import com.aiecommerce.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .department(request.getDepartment())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> search(String name, String role, String department) {
        return userRepository.search(normalize(name), normalize(role), normalize(department));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User update(Long id, UserRequest request) {
        User existing = getById(id);
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setRole(request.getRole());
        existing.setDepartment(request.getDepartment());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        User existing = getById(id);
        userRepository.delete(existing);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
