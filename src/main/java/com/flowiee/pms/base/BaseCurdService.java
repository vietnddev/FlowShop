package com.flowiee.pms.base;

import java.util.List;

public interface BaseCurdService<T> {
    List<T> findAll();

    T findById(Long pEntityId, boolean pThrowException);

    T save(T pEntity);

    T update(T pEntity, Long pEntityId);

    String delete(Long pEntityId);
}