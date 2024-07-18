package ru.practicum.explorewithme.ewmservice.user.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.ewmservice.user.model.User;

import java.util.List;

public interface UserDao extends JpaRepository<User, Long> {
    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    Page<User> findAll(Pageable pageable);
}
