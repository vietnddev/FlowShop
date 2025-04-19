package com.flowiee.pms.common.base;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventListener implements PostUpdateEventListener {
    @Override
    public void onPostUpdate(PostUpdateEvent event) {

    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}