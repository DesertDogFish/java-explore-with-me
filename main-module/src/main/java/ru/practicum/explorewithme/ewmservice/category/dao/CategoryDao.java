package ru.practicum.explorewithme.ewmservice.category.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewmservice.category.model.Category;

@Repository
public interface CategoryDao extends JpaRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);
}
