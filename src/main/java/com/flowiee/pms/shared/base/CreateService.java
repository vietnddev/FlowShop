package com.flowiee.pms.shared.base;

public interface CreateService<T> {
    T create(T entity);
}