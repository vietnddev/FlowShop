package com.flowiee.pms.validate.authorize.storage;

public interface IVldModuleStorage {
    boolean dashboard(boolean throwException);

    boolean readStorage(boolean throwException);

    boolean insertStorage(boolean throwException);

    boolean updateStorage(boolean throwException);

    boolean deleteStorage(boolean throwException);
}