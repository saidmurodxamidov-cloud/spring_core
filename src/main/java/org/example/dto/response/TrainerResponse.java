package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerResponse {
    private String username;
    private String firstname;
    private String lastname;
    private List<TrainingTypeResponse> trainingTypeResponse;
}
