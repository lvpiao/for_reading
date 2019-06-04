package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;

import exceptions.CustomException;
import models.JR;
import models.ResultModel;
import models.User;
import redis.clients.jedis.Jedis;
import services.CommonService;
import services.UserService;
import utils.DataUtil;
import utils.EmailUtil;
import utils.HintUtil;
import utils.Manager;
import utils.TokenUtil;

@Controller
public class CommonController {

	@Autowired
	CommonService commonService;
	@Autowired
	UserService userService;

	@RequestMapping(value = "/login")
	public @ResponseBody JSONObject login(@RequestParam(name = "uid") String uid,
			@RequestParam(name = "password") String password)
			throws IOException, InterruptedException, ExecutionException, CustomException {
		if (StringUtils.isAnyEmpty(uid, password)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		if (commonService.login(uid, password)) {
			// 生成token
			String newToken = TokenUtil.createTokenByUid(uid);
			ResultModel res = new ResultModel(JR.SUCCESS);
			res.setToken(newToken);
			return res;
		} else {
			return new ResultModel(JR.FAIL, HintUtil.PASSWORD_ERROR.getMsg());
		}
	}

	@RequestMapping(value = "/login/{token:.+}")
	public @ResponseBody JSONObject loginWithToken(@PathVariable("token") String token)
			throws IOException, InterruptedException, ExecutionException, CustomException {
		if (StringUtils.isNotEmpty(token)) {
			DecodedJWT jwt = TokenUtil.verifyToken(token);
			if (jwt == null) {
				return new ResultModel(JR.FAIL);
			} else {
				return new ResultModel(JR.SUCCESS);
			}
		} else {
			return new ResultModel(JR.FAIL);
		}
	}

	@RequestMapping(value = "/signup")
	public @ResponseBody JSONObject signup(@RequestParam(name = "uid") String uid,
			@RequestParam(name = "password") String password, HttpServletRequest request)
			throws IOException, InterruptedException, ExecutionException, CustomException {
		if (StringUtils.isAnyEmpty(uid, password)) {
			ResultModel res = new ResultModel(JR.FAIL);
			res.setMessage(HintUtil.LOST_PARA.getMsg());
			return res;
		}
		if (!EmailUtil.isEmailString(uid)) {
			ResultModel res = new ResultModel(JR.FAIL);
			res.setMessage(HintUtil.PARA_ERROR.getMsg());
			return res;
		}
		if (password.length() < DataUtil.MIN_PASSWRD_LENGTH) {
			ResultModel res = new ResultModel(JR.FAIL);
			res.setMessage(HintUtil.PARA_ERROR.getMsg());
			return res;
		}
		if (commonService.existUser(uid)) {
			ResultModel res = new ResultModel(JR.FAIL);
			res.setMessage(HintUtil.USER_ALREADY_EXISTS.getMsg());
			return res;
		}
		// 发送确认邮件
		Jedis jedis = Manager.getResource();
		try {
			String randToken = UUID.randomUUID().toString().replace("-", "");
			String verifyUrl = DataUtil.SERVER + "/verify/" + randToken;
			User user = new User(uid, password);
			// 设置注册缓存及过期时间token
			jedis.set(randToken, JSON.toJSONString(user));
			jedis.expire(randToken, (int) TimeUnit.MINUTES.toSeconds(5));
			EmailUtil.sendVerifySignupEmail(randToken, uid, verifyUrl);
		} catch (EmailException e) {
			ResultModel res = new ResultModel(JR.FAIL);
			res.setMessage(HintUtil.SERVER_ERROR.getMsg());
			e.printStackTrace();
			return res;
		} finally {
			jedis.close();
		}
		ResultModel res = new ResultModel(JR.SUCCESS);
		return res;
	}

	@RequestMapping(value = "/verify/{randToken}")
	public @ResponseBody JSONObject verify(@PathVariable(name = "randToken") String randToken)
			throws IOException, InterruptedException, ExecutionException, CustomException {
		if (StringUtils.isEmpty(randToken)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		Jedis jedis = Manager.getResource();
		try {
			if (jedis.exists(randToken)) {
				String userJson = jedis.get(randToken);
				User user = JSON.parseObject(userJson, User.class);
				if (userService.insert(user) > 0 && jedis.del(randToken) > 0) {
					return new ResultModel(JR.SUCCESS);
				} else {
					return new ResultModel(JR.FAIL, HintUtil.OPERATE_FIAL.getMsg());
				}
			} else {
				return new ResultModel(JR.FAIL, HintUtil.EXPIRD_URL.getMsg());
			}
		} finally {
			jedis.close();
		}
	}

	@RequestMapping(value = "/userInfo/{token:.+}")
	public @ResponseBody ResultModel userInfo(@PathVariable("token") String token) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		User user = userService.getUserById(uid);
		String userText = JSON.toJSONString(user);
		return new ResultModel(JR.SUCCESS, userText);
	}

	@RequestMapping(value = "/changePassword/{token:.+}")
	public @ResponseBody ResultModel changePassword(@PathVariable("token") String token,
			@RequestParam("originPassword") String originPassword, @RequestParam("originPassword") String newPassword) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		if (StringUtils.isAnyEmpty(originPassword, newPassword)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		if (commonService.login(uid, originPassword) && userService.changePassword(uid, newPassword) > 0) {
			return new ResultModel(JR.SUCCESS);
		} else {
			return new ResultModel(JR.FAIL, HintUtil.OPERATE_FIAL.getMsg());
		}
	}

	@RequestMapping(value = "/currentVersion/{token:.+}")
	public @ResponseBody ResultModel currentVersion(@PathVariable("token") String token) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		return new ResultModel(JR.SUCCESS, String.valueOf(DataUtil.curClientVersion));
	}

	@RequestMapping(value = "/downloadNewVersionClient/{token:.+}")
	public @ResponseBody ResultModel downloadNewVersionClient(@PathVariable("token") String token,
			HttpServletResponse response) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		try (FileInputStream fi = new FileInputStream(DataUtil.getNewClientPath())) {
			response.setHeader("Content-Disposition", "attachment; filename=forReading.apk");
			response.setContentLengthLong(fi.available());
			IOUtils.copy(fi, response.getOutputStream());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/changeNickname/{token:.+}")
	public @ResponseBody ResultModel changeNickname(@PathVariable("token") String token,
			@RequestParam("nickename") String nickename) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		if (StringUtils.isAnyEmpty(nickename)) {
			return new ResultModel(JR.FAIL, HintUtil.LOST_PARA.getMsg());
		}
		if (userService.changeNickname(uid, nickename) > 0) {
			return new ResultModel(JR.SUCCESS);
		} else {
			return new ResultModel(JR.FAIL, HintUtil.OPERATE_FIAL.getMsg());
		}
	}

}
