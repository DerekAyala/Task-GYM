package com.epam.taskgym.storage;

import com.epam.taskgym.entity.Training;
import org.springframework.stereotype.Service;

@Service
public class TrainingInMemoryDb extends InMemoryDb<Training> {
}
