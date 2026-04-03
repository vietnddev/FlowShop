package com.flowiee.pms.shared.base;

import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.enums.CATEGORY;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Getter
public class BaseController extends BaseAuthorize {
	@Getter
	@Setter
	class SearchTool {
		private boolean enableFilter;
		private List<String> filters;

		SearchTool() {
			enableFilter = false;
		}
	}

	private SearchTool searchTool;

	protected ModelAndView baseView(ModelAndView modelAndView) {
		SearchTool searchTool = getSearchTool();
		modelAndView.addObject("configSearchTool", searchTool != null ? searchTool : new SearchTool());
		modelAndView.addObject("USERNAME_LOGIN", SecurityUtils.getCurrentUser().getUsername());
		setURLHeader(modelAndView);
		setURLSidebar(modelAndView);
		return modelAndView;
	}

	protected ModelAndView refreshPage(HttpServletRequest request) {
		return new ModelAndView("redirect:" + request.getHeader("referer"));
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