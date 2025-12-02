package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.output.CategoryDto;
import ru.practicum.client.category.CategoryFeignClient;
import ru.practicum.service.CategoryService;
import ru.practicum.user.output.UserDto;

import java.util.List;

@RestController
@RequestMapping("internal/api/categories")
@RequiredArgsConstructor
public class CategoryClientController implements CategoryFeignClient {
    private final CategoryService service;

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/by-ids")
    public List<CategoryDto> getByIds(@RequestParam @UniqueElements List<Long> ids) {
        return service.getByIds(ids);
    }
}