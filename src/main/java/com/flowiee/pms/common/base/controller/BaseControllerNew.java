package com.flowiee.pms.common.base.controller;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.common.security.UserSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Getter
public abstract class BaseControllerNew<D> {
    @Autowired
    protected UserSession mvUserSession;
    @Autowired
    ControllerHelper mvCHelper;

    @Getter
    @Setter
    class SearchTool {
        private boolean enableFilter;
        private List<String> filters;

        SearchTool() {
            enableFilter = false;
        }
    }

    protected Logger mvLogger = LoggerFactory.getLogger(getClass());
    private SearchTool searchTool;

    protected abstract BaseGService<?, D, ?> getService();

    /*
     * The following methods provide default handlers for basic CRUD endpoints.
     * Subclasses can expose them as needed by calling these methods from their own mapped endpoints.
     */

    protected AppResponse<List<D>> handleFindAll() {
        try {
            return mvCHelper.success(getService().findAll());
        } catch (RuntimeException ex) {
            throw new AppException("Error fetching data", ex);
        }
    }

    protected AppResponse<D> handleFindById(Long pId) {
        try {
            return mvCHelper.success(getService().findById(pId, true));
        } catch (RuntimeException ex) {
            throw new AppException("Entity not found with ID " + pId, ex);
        }
    }

    protected AppResponse<D> handleCreate(D pDto) {
        try {
            return mvCHelper.success(getService().save(pDto));
        } catch (RuntimeException ex) {
            throw new AppException("Error creating entity", ex);
        }
    }

    protected AppResponse<D> handleUpdate(D dto, Long pId) {
        try {
            return mvCHelper.success(getService().update(dto, pId));
        } catch (RuntimeException ex) {
            throw new AppException("Error updating entity with ID " + pId, ex);
        }
    }

    protected AppResponse<String> handleDelete(Long pId) {
        try {
            return mvCHelper.success(getService().delete(pId));
        } catch (RuntimeException ex) {
            throw new AppException("Error deleting entity with ID " + pId, ex);
        }
    }

    protected ModelAndView baseView(ModelAndView modelAndView) {
        SearchTool searchTool = getSearchTool();
        modelAndView.addObject("configSearchTool", searchTool != null ? searchTool : new SearchTool());
        modelAndView.addObject("USERNAME_LOGIN", mvUserSession.getUserPrincipal().getUsername());
        setURLHeader(modelAndView);
        setURLSidebar(modelAndView);
        return modelAndView;
    }

    protected ModelAndView refreshPage(HttpServletRequest request) {
        return  new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    private void setURLHeader(ModelAndView modelAndView) {
        for (Map.Entry<String, String> entry : CommonUtils.mvEndPointHeaderConfig.entrySet()) {
            modelAndView.addObject(entry.getKey(), entry.getValue());
        }
    }

    private void setURLSidebar(ModelAndView modelAndView) {
        for (Map.Entry<String, String> entry : CommonUtils.mvEndPointSideBarConfig.entrySet()) {
            modelAndView.addObject(entry.getKey(), entry.getValue());
        }
    }

    protected void setupSearchTool(boolean pEnableFilter, List<Object> pFilters) {
        searchTool = new SearchTool();
        searchTool.setEnableFilter(pEnableFilter);
        List<String> filters = new ArrayList<>();
        if (pFilters != null) {
            for (Object obj : pFilters) {
                if (obj instanceof CATEGORY) {
                    filters.add(((CATEGORY) obj).name());
                } else if (obj instanceof String) {
                    filters.add(obj.toString());
                }
            }
        }
        searchTool.setFilters(filters);
    }
}