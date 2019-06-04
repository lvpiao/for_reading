package controllers;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import models.JR;
import models.ResultModel;
import services.UserFavoriteTagService;
import utils.HintUtil;
import utils.DataUtil;
import utils.TokenUtil;

@Controller
@RequestMapping("tag")
public class TagController {
	@RequestMapping(value = "/getAllTags")
	/**
	 * 返回所有 标签分类
	 */
	public @ResponseBody JSONObject getTags(@RequestParam("token") String token) {
		if (TokenUtil.verifyToken(token) != null) {
			return new ResultModel(JR.SUCCESS, (JSONArray) JSON.toJSON(DataUtil.getAllTags()));
		} else {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
	}

	@Autowired
	UserFavoriteTagService userFavoriteTagService;

	@RequestMapping(value = "/choseTags")
	/**
	 * 用户选择了标签
	 */
	public @ResponseBody JSONObject choseTags(@RequestParam("token") String token,
			@RequestParam("chose") String chose) {
		if (StringUtils.isAnyEmpty(token, chose)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid != null) {
			JSONObject jsonObject = JSON.parseObject(chose);
			JSONArray jsonArray = jsonObject.getJSONArray("chose");
			if (jsonArray != null && jsonArray.size() >= 0)
				userFavoriteTagService.deleteTagsByUid(uid);
			for (int i = 0; i < jsonArray.size(); i++) {
				userFavoriteTagService.insert(uid, jsonArray.getString(i));
			}
			return new ResultModel(JR.SUCCESS);
		} else {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
	}

	@RequestMapping(value = "/existsTags")
	/**
	 * 用户是否已经选择了标签
	 */
	public @ResponseBody JSONObject existsTags(@RequestParam("token") String token) {
		if (StringUtils.isEmpty(token)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid != null) {
			if (userFavoriteTagService.getTagsCountByUid(uid) > 0) {
				return new ResultModel(JR.SUCCESS);
			} else {
				return new ResultModel(JR.FAIL);
			}
		} else {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
	}

	@RequestMapping(value = "/getChoseTags")
	/**
	 * 获取用户选择的标签
	 */
	public @ResponseBody JSONObject getChoseTags(@RequestParam("token") String token) {
		if (StringUtils.isEmpty(token)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid != null) {
			List<Map<String, String>> uft = userFavoriteTagService.getTagsByUidAsMap(uid);
			return new ResultModel(JR.SUCCESS, uft);
		} else {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
	}

}
