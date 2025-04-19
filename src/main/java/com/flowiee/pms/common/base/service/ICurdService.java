package com.flowiee.pms.common.base.service;

import java.util.List;

public interface ICurdService<T> {
    List<T> findAll();

    T findById(Long pEntityId, boolean pThrowException);

    T save(T pEntity);

    T update(T pEntity, Long pEntityId);

    String delete(Long pEntityId);
}