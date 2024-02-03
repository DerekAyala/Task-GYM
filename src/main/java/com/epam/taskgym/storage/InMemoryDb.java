package com.epam.taskgym.storage;

import java.util.Map;

public interface InMemoryDb {
    Map<Long, Object> getStorage();
}
