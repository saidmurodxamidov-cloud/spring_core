package org.example.model;

import lombok.*;

import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Trainer extends UserDTO{
    private Set<TrainingTypeDTO> specialization;

}
