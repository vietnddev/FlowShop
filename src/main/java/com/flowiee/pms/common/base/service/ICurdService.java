package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.model.BaseParameter;

import java.util.List;

public interface ICurdService<T> {
    T findById(Long pEntityId, boolean pThrowException);

    T save(T pEntity);

    T update(T pEntity, Long pEntityId);

    String delete(Long pEntityId);
}