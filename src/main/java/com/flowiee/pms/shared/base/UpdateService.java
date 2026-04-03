package com.flowiee.pms.shared.base;

public interface UpdateService<T> {
    T update(T entity, Long id);
}