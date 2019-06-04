package controllers;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import models.JR;
import models.ResultModel;
import models.User;
import services.CommonService;
import services.UserService;
import utils.DataUtil;
import utils.HintUtil;
import utils.TokenUtil;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	CommonService commonService;
	@Autowired
	UserService userService;

	@RequestMapping(value = "/addSpiderTag/{token:.+}")
	public @ResponseBody ResultModel addSpiderTag(@PathVariable("token") String token,
			@RequestParam("newTag") String newTag) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		User user = userService.getUserById(uid);
		if (!user.isAdmin())
			return new ResultModel(JR.FAIL, HintUtil.PERMISSION_DENY.getMsg());
		DataUtil.addTags(new HashSet<String>(Arrays.asList(newTag)));
		return new ResultModel(JR.SUCCESS);
	}

	@RequestMapping(value = "/removeSpiderTag/{token:.+}")
	public @ResponseBody ResultModel removeSpiderTag(@PathVariable("token") String token,
			@RequestParam("newTag") String newTag) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		User user = userService.getUserById(uid);
		if (!user.isAdmin())
			return new ResultModel(JR.FAIL, HintUtil.PERMISSION_DENY.getMsg());
		DataUtil.removeTags(new HashSet<String>(Arrays.asList(newTag)));
		return new ResultModel(JR.SUCCESS);
	}

	@RequestMapping(value = "/setClientVersion/{token:.+}")
	public @ResponseBody ResultModel setClientVersion(@PathVariable("token") String token,
			@RequestParam(name = "version", defaultValue = "0") int version) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		User user = userService.getUserById(uid);
		if (!user.isAdmin())
			return new ResultModel(JR.FAIL, HintUtil.PERMISSION_DENY.getMsg());
		DataUtil.curClientVersion++;
		File file = new File(DataUtil.getNewClientPath());
		if (file.exists()) {
			return new ResultModel(JR.SUCCESS);
		} else {
			DataUtil.curClientVersion--;
			return new ResultModel(JR.FAIL, "未发现新的安装包文件");
		}

	}
}
