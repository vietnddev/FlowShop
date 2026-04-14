package com.flowiee.pms.shared.base;

public interface ICurdService<T> {
    T findById(Long pEntityId, boolean pThrowException);

    T save(T pEntity);

    T update(T pEntity, Long pEntityId);

    boolean delete(Long pEntityId);
}