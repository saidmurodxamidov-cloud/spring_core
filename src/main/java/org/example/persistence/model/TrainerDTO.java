package org.example.persistence.model;

import lombok.*;

import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDTO extends UserDTO {
    private Set<TrainingTypeDTO> specialization;

}
