package ru.practicum.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetStatisticDto {
    private String app;
    private String uri;
    private Long hits;
}
