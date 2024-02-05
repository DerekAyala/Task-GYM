package com.epam.taskgym.storage;

import com.epam.taskgym.entity.BaseIdEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryDb<T extends BaseIdEntity> {
    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private static Long count = 1L;

    public Map<Long, T> getStorage() {
        return new HashMap<>(storage);
    }

    public Collection<T> findAll() {
        return storage.values();
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public T save(T data) {
        data.setId(count);
        storage.put(count++, data);
        return data;
    }

    public T update(T data) {
        if (data.getId() != null && storage.containsKey(data.getId())) {
            storage.replace(data.getId(), data);
            return data;
        } else {
            throw new IllegalArgumentException("The specified id does not exist");
        }
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}
