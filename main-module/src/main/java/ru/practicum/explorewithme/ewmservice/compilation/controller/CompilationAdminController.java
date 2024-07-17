package ru.practicum.explorewithme.ewmservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.UpdateCompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {
        return compilationService.save(dto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Long compId,
                                 @Valid @RequestBody UpdateCompilationDto dto) {
        return compilationService.update(compId, dto);
    }
}
