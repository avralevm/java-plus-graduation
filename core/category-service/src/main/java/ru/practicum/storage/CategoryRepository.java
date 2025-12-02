package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    List<Category> findAllByIdIn(List<Long> ids);
}
