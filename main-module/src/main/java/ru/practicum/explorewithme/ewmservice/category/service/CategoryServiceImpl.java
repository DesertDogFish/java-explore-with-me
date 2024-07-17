package ru.practicum.explorewithme.ewmservice.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryShortDto;
import ru.practicum.explorewithme.ewmservice.category.mapper.CategoryDtoMapper;
import ru.practicum.explorewithme.ewmservice.category.mapper.CategoryShortDtoMapper;
import ru.practicum.explorewithme.ewmservice.category.model.Category;
import ru.practicum.explorewithme.ewmservice.category.dao.CategoryDao;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.common.PageRequestHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;
    private final CategoryShortDtoMapper categoryShortDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;

    @Transactional
    @Override
    public CategoryDto save(CategoryShortDto categoryShortDto) {
        return Optional.of(categoryShortDto)
                .map(categoryShortDtoMapper::toModel)
                .map(categoryDao::save)
                .map(categoryDtoMapper::toDto)
                .get();
    }

    @Transactional
    @Override
    public CategoryDto patch(Long id, CategoryShortDto dto) {
        Category category = categoryDao.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", id)));
        if (category.getName().equals(dto.getName())) {
            return categoryDtoMapper.toDto(category);
        }
        return save(dto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!categoryDao.existsById(id)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", id));
        }
        categoryDao.deleteById(id);
    }

    @Override
    public CategoryDto get(Long id) {
        return categoryDao.findById(id)
                .map(categoryDtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
    }

    @Override
    public List<CategoryDto> get(Integer from, Integer size) {
        return categoryDao.findAll(new PageRequestHelper(from, size, null))
                .getContent()
                .stream()
                .map(categoryDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
