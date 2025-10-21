package ru.practicum.users.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    String email;
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}
