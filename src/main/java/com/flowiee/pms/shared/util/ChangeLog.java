package com.flowiee.pms.shared.util;

import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.entity.SystemConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class ChangeLog {
    @Setter
    private Object oldObject;
    @Setter
    private Object newObject;

    private Map<String, Object[]> logChanges = new HashMap<>();
    private String oldValues;
    private String newValues;

    public ChangeLog() {}

    public ChangeLog(Object pOldObject) {
        this.oldObject = pOldObject;
    }

    public ChangeLog(Object pOldObject, Object pNewObject) {
        this.oldObject = pOldObject;
        this.newObject = pNewObject;
    }

    public Map<String, Object[]> doAudit() {
        if (oldObject == null)
            throw new NullPointerException("oldObject is null!");

        if (newObject == null)
            throw new NullPointerException("newObject is null!");

        Map<String, Object[]> changes = new HashMap<>();
        StringBuilder oldValueBuilder = new StringBuilder("Fields: ");
        StringBuilder newValueBuilder = new StringBuilder("Fields: ");
        for (Field field : oldObject.getClass().getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                String extraSystemConfigInfo = "";

                if (oldValue instanceof Category) {
                    oldValue = ((Category) oldValue).getName();
                    newValue = ((Category) newValue).getName();
                } else if (oldObject instanceof SystemConfig) {
                    extraSystemConfigInfo = ((SystemConfig) oldObject).getCode() + " - ";
                }

                if (!Objects.equals(oldValue, newValue)) {
                    changes.put(field.getName(), new Object[] {oldValue, newValue});

                    oldValueBuilder.append(extraSystemConfigInfo).append(field.getName()).append(" (").append(ObjectUtils.isNotEmpty(oldValue) ? oldValue.toString() : " ").append("); ");
                    newValueBuilder.append(extraSystemConfigInfo).append(field.getName()).append(" (").append(ObjectUtils.isNotEmpty(newValue) ? newValue.toString() : " ").append("); ");
                }
            } catch (IllegalAccessException e) {
                log.error("Error checking changed entity fields {}", oldObject.getClass().getName(), e);
            }
        }

        if (oldValueBuilder.toString().equals("Fields: "))
            oldValueBuilder = new StringBuilder("Nothing change");
        if (newValueBuilder.toString().equals("Fields: "))
            newValueBuilder = new StringBuilder("Nothing change");

        this.oldValues = formatValueChange(oldValueBuilder.toString());
        this.newValues = formatValueChange(newValueBuilder.toString());
        this.logChanges = changes;

        return logChanges;
    }

    private String formatValueChange(String valueChange) {
        if (valueChange != null && valueChange.endsWith("; ")) {
            valueChange = valueChange.substring(0, valueChange.length() - 2);
        }
        return valueChange;
    }
}