package controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import exceptions.CustomException;
import models.JR;

@Controller
public class GlobalExceptionController implements HandlerExceptionResolver {

	@RequestMapping(value = "/error")
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {
		// 自定义异常
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (e instanceof CustomException) {
			// 错误信息
			String message = e.getMessage();
			if (StringUtils.isNotEmpty(message))
				map.put(JR.K_MSG, message);
			else {
				map.put(JR.K_MSG, "未知错误！");
				map.put(JR.K_RESULT, JR.V_FAIL);
			}
		} else {
			map.put(JR.K_RESULT, JR.V_FAIL);
			map.put(JR.K_MSG, "请求错误！！！");
		}
		try {
			response.setStatus(Status.OK.getStatusCode());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().append(JSON.toJSONString(map));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
