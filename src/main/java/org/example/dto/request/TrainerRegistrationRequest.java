package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerRegistrationRequest {
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    private List<String> specialization;
}

