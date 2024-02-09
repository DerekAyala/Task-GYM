    package com.epam.taskgym.service;

    import com.epam.taskgym.dto.TrainerDTO;
    import com.epam.taskgym.entity.Trainer;
    import com.epam.taskgym.entity.TrainingType;
    import com.epam.taskgym.entity.User;
    import com.epam.taskgym.repository.TrainerRepository;
    import com.epam.taskgym.repository.UserRepository;
    import com.epam.taskgym.service.exception.FailAuthenticateException;
    import com.epam.taskgym.service.exception.MissingAttributes;
    import com.epam.taskgym.service.exception.NotFoundException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.util.Map;
    import java.util.Optional;


    @Service
    public class TrainerService {

        @Autowired
        private TrainerRepository trainerRepository;

        @Autowired
        private UserService userService;

        @Autowired
        private TrainingTypeService trainingTypeService;

        private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);


        public boolean authenticateTrainer(String username, String password) {
            return trainerRepository.findByUserUsernameAndUserPassword(username, password).isPresent();
        }

        public Optional<Trainer> getTrainerByUsername(String username) {
            return trainerRepository.findByUserUsername(username);
        }

        public TrainerDTO registerTrainer(Map<String, String> trainerDetails) {
            if (trainerDetails == null || trainerDetails.isEmpty()) {
                throw new MissingAttributes("Trainer details cannot be null or empty");
            }
            User user = userService.createUser(trainerDetails);

            Trainer trainer = new Trainer();
            trainer.setUser(user);
            if ((!trainerDetails.containsKey("specialization") || trainerDetails.get("specialization").isEmpty())) {
                throw new MissingAttributes("specialization is required");
            }

            Optional<TrainingType> specialization = trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));

            if (specialization.isEmpty()) {
                throw new NotFoundException("Specialization with name {" + trainerDetails.get("specialization") + "} found");
            }

            trainer.setSpecialization(specialization.get());
            trainerRepository.save(trainer);

            return fillTrainerDTO(user, trainer);
        }

        public TrainerDTO updateTrainer(Map<String, String> trainerDetails, String username, String password) {
            if (trainerDetails == null || trainerDetails.isEmpty()) {
                throw new MissingAttributes("Trainer Update details cannot be null or empty");
            }
            if (authenticateTrainer(username, password)) {
                Optional<Trainer> trainerOptional = getTrainerByUsername(username);
                if (trainerOptional.isPresent()) {
                    Trainer trainer = trainerOptional.get();
                    User user = userService.updateUser(trainerDetails, trainer.getUser());
                    trainer.setUser(user);
                    if (trainerDetails.containsKey("specialization") && !trainerDetails.get("specialization").isEmpty()) {
                        Optional<TrainingType> specialization = trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));
                        if (specialization.isEmpty()) {
                            throw new NotFoundException("Specialization with name {" + trainerDetails.get("specialization") + "} found");
                        }
                        trainer.setSpecialization(specialization.get());
                    }
                    trainerRepository.save(trainer);
                    return fillTrainerDTO(user, trainer);
                }
            } else {
                throw new FailAuthenticateException("Fail to authenticate");
            }
            return null;
        }

        public boolean updatePasssword(String username, String password, String newPassword) {
            if (authenticateTrainer(username, password)) {
                Optional<Trainer> trainerOptional = getTrainerByUsername(username);
                if (trainerOptional.isPresent()) {
                    Trainer trainer = trainerOptional.get();
                    User user = trainer.getUser();
                    user.setPassword(newPassword);
                    userService.saveUser(user);
                    trainer.setUser(user);
                    trainerRepository.save(trainer);
                    return true;
                }
            }
            return false;
        }

        private TrainerDTO fillTrainerDTO(User user, Trainer trainer) {
            return new TrainerDTO(user, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainer, trainer.getSpecialization());
        }
    }
