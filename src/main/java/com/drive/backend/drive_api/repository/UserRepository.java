package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private static List<UserDto> users = Collections.synchronizedList(new ArrayList<>(List.of(
            new UserDto("testuser", "testpass", "test@example.com")
    )));
    private static AtomicLong nextId = new AtomicLong(1);

    public Optional<UserDto> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public UserDto save(UserDto user) {

        if (findByUsername(user.getUsername()).isPresent()) {

            return findByUsername(user.getUsername()).get();
        } else {
            users.add(user);
        }
        return user;
    }
}