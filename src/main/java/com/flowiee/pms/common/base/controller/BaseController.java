package com.flowiee.pms.common.base.controller;

import com.flowiee.pms.common.base.BaseAuthorize;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.security.UserSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class BaseController extends BaseAuthorize {
	@Autowired
	protected UserSession mvUserSession;

	@Getter
	@Setter
	class SearchTool {
		private boolean enableFilter;
		private List<String> filters;

		SearchTool() {
			enableFilter = false;
		}
	}

	protected Logger   mvLogger = LoggerFactory.getLogger(getClass());
	private SearchTool searchTool;

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