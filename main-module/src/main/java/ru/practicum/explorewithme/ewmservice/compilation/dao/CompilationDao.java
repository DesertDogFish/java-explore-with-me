package ru.practicum.explorewithme.ewmservice.compilation.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import ru.practicum.explorewithme.ewmservice.compilation.model.Compilation;

public interface CompilationDao extends JpaRepository<Compilation, Long> {
    @Query ("SELECT c FROM Compilation c " +
            "WHERE (:pinned IS NULL OR c.pinned IS :pinned)")
    Page<Compilation> findAllCompilationsOptionalPinned(@Nullable Boolean pinned, Pageable pageable);
}
