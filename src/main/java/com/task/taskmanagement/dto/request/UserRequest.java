package com.task.taskmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
                          @NotBlank(message = "User name is required")
                          @Size(max = 150, message = "User name must not exceed 150 characters")
                          String name,

                          @NotBlank(message = "Email is required")
                          @Email(message = "Email must be valid")
                          @Size(max = 254, message = "Email must not exceed 254 characters")
                          String email,

                          @NotBlank(message = "Role is required")
                          @Size(max = 50, message = "Role must not exceed 50 characters")
                          String role
) {
}
