package com.flowiee.pms.shared.base;

public interface FindService<T> {
    T findById(Long pEntityId,  boolean pThrowException);
}