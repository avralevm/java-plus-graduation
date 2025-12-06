package ru.practicum.client.category;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.category.output.CategoryDto;

import java.util.List;

@Component
public class CategoryFeignClientFallback implements CategoryFeignClient {
    @Override
    public CategoryDto getCategoryById(Long id) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: CategoryService не доступен");
    }

    @Override
    public List<CategoryDto> getByIds(List<Long> ids) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: CategoryService не доступен");
    }
}