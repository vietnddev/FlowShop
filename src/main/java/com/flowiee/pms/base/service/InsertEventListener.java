package com.flowiee.pms.base.service;

import com.flowiee.pms.entity.system.EventLog;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class InsertEventListener implements PostInsertEventListener {
    @Override
    public void onPostInsert(PostInsertEvent event) {

    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}