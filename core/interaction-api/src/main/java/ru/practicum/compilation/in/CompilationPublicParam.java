package ru.practicum.compilation.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompilationPublicParam {
    Boolean pinned;
    Integer from;
    Integer size;
}
