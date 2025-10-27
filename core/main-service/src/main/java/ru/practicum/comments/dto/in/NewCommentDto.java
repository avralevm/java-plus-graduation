package ru.practicum.comments.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NewCommentDto {
    @NotBlank
    @Length(min = 20, max = 7000)
    String text;
}