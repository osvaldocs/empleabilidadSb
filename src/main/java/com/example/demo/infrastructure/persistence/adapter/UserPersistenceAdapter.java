package com.example.demo.infrastructure.persistence.adapter;

import com.example.demo.domain.model.User;
import com.example.demo.domain.port.out.UserRepositoryPort;
import com.example.demo.infrastructure.persistence.entity.UserEntity;
import com.example.demo.infrastructure.persistence.mapper.UserMapper;
import com.example.demo.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(UserMapper::toDomain);
    }
}
