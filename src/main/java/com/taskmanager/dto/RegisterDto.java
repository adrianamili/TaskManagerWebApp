package com.taskmanager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {

    @NotBlank(message = "Username-ul este obligatoriu")
    @Size(min = 3, max = 50, message = "Username-ul trebuie sa aiba intre 3 si 50 de caractere")
    private String username;

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Email-ul nu este valid")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 6, message = "Parola trebuie sa aiba cel putin 6 caractere")
    private String password;

    @NotBlank(message = "Confirmarea parolei este obligatorie")
    private String confirmPassword;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
