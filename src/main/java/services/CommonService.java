package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import models.User;
import utils.DataUtil;

@Service
public class CommonService {

	@Autowired
	UserService userService;

	public boolean login(String uid, String password) {
		Integer t = userService.login(uid, password);
		return t != null && t == 1;
	}

	public boolean existUser(String id) {
		Integer t = userService.exist(id);
		return t != null && t == 1;
	}

	public boolean isAdmin(String uid, String password) {
		User user = userService.getUser(uid, password);
		return user.getPermission() > DataUtil.USER;
	}
}
