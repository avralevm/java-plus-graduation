package ru.practicum.compilation.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);

    List<Compilation> findByPinned(Boolean pinned);
}
