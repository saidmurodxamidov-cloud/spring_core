package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.mapper.TrainerMapper;
import org.example.model.TrainerDTO;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.repository.UserRepository;
import org.example.util.UsernameGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerEntityService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final TrainingService trainingService;
    private final PasswordEncoder passwordEncoder;
    private final TrainerMapper trainerMapper;

    public TrainerDTO createTrainer(TrainerDTO trainerDTO){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = bcrypt.encode(String.valueOf(trainerDTO.getPassword()));
        String username = UsernameGenerator.generateUsername(trainerDTO.getFirstName(),trainerDTO.getLastName(),availableUsernames);
        log.info("creating trainer with username {}", username);
        trainerDTO.setPassword(password.toCharArray());
        trainerDTO.setUserName(username);

        TrainerEntity trainer = trainerMapper.toEntity(trainerDTO);
        trainerRepository.save(trainer);
        log.info("trainer {} created successfully", username);
        return trainerDTO;
    }
    public boolean authenticate(String username, String password) {
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Trainer not found: " + username));


        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is not active: " + username);
        }


        if (!bcrypt.matches(password, String.valueOf(trainer.getUser().getPassword()))) {
            throw new BadCredentialsException("Wrong password for trainer: " + username);
        }

        return true;
    }

    public TrainerDTO getTrainerByUsername(String username){
        log.debug("getting trainer: {}" , username);
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("trainer " + username + " does not exist"));
        return trainerMapper.toDTO(trainer);
    }
}
