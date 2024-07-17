package ru.practicum.explorewithme.ewmservice.compilation.service;

import ru.practicum.explorewithme.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto get(Long compId);

    List<CompilationDto> get(Boolean pinned, Integer from, Integer size);

    CompilationDto save(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationDto updateCompilation);
}
