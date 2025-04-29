package com.flowiee.pms.common.security;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> customParams = new HashMap<>();

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setParameter(String name, String value) {
        customParams.put(name, new String[]{value});
    }

    @Override
    public String getParameter(String name) {
        if (customParams.containsKey(name)) {
            return customParams.get(name)[0];
        }
        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (customParams.containsKey(name)) {
            return customParams.get(name);
        }
        return super.getParameterValues(name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("customParams", customParams)
                .toString();
    }
}