package com.epam.taskgym.storage;

import com.epam.taskgym.entity.Trainer;
import org.springframework.stereotype.Service;

@Service
public class TrainerInMemoryDb extends InMemoryDb<Trainer> {
}