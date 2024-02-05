package com.epam.taskgym.storage;

import com.epam.taskgym.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserInMemoryDb extends InMemoryDb<User> {
}
