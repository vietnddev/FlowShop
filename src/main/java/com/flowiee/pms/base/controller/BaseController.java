package com.flowiee.pms.base.controller;

import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.base.auth.BaseAuthorize;
import com.flowiee.pms.security.UserSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

	protected static <T> AppResponse<T> success(@NonNull T data) {
		return success(data, "OK");
	}

	protected static <T> AppResponse<T> success(@NonNull T data, String message) {
		return success(data, message, null);
	}

	protected static <T> AppResponse<T> success(@NonNull T data, int pageNum, int pageSize, int totalPage, long totalElements) {
		return success(data, "OK", pageNum, pageSize, totalPage, totalElements);
	}

	protected static <T> AppResponse<T> success(@NonNull T data, String message, int pageNum, int pageSize, int totalPage, long totalElements) {
		return success(data, message, new AppResponse.PaginationModel(pageNum, pageSize, totalPage, totalElements));
	}

	protected static <T> AppResponse<T> success(@NonNull T data, String message, AppResponse.PaginationModel pagination) {
		return new AppResponse<>(true, HttpStatus.OK, message, data, pagination);
	}

	protected static <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus) {
		return fail(httpStatus, "NOK");
	}

	protected static <T> AppResponse<T> fail(@NonNull HttpStatus httpStatus, @NonNull String message) {
		return new AppResponse<>(false, httpStatus, message, null, null);
	}

	protected static <T> AppResponse<T> badRequest(String message) {
		return new AppResponse<>(false, HttpStatus.BAD_REQUEST, message, null, null);
	}

	protected static <T> AppResponse<T> internalServer(String message) {
		return new AppResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, message, null, null);
	}

	protected static <T> AppResponse<T> internalServer(String message, Throwable trace) {
		if (trace == null) {
			return internalServer(message);
		}
		trace.printStackTrace();
		return new AppResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, message, null, null);
	}
}