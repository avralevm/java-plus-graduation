package ru.practicum.client.category;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.output.CategoryDto;
import ru.practicum.client.FeignConfig;

import java.util.List;

@FeignClient(name = "category-service",
        path = "internal/api/categories",
        configuration = FeignConfig.class,
        fallback = CategoryFeignClientFallback.class)
public interface CategoryFeignClient {
    @GetMapping("/{id}")
    CategoryDto getCategoryById(@PathVariable Long id);

    @GetMapping("/by-ids")
    List<CategoryDto> getByIds(@RequestParam @UniqueElements List<Long> ids);
}