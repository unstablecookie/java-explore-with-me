package ru.practicum.ewm.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilations.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinned(Boolean pinned, Pageable page);

    Page<Compilation> findAll(Pageable page);
}
