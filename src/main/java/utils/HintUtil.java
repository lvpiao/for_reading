package utils;

public enum HintUtil {

	PASSWORD_ERROR(1, "账号或者密码错误"), LOST_PARA(2, "缺少参数"), OPERATE_FIAL(3, "请求失败,请重试"), USER_ALREADY_EXISTS(4, "用户名已存在!"),
	SERVER_ERROR(5, "服务器异常"), PARA_ERROR(6, "请求失败,请重试"), EXPIRD_URL(7, "过期的链接"), INVALID_TOKEN(8, "无效的token"),
	PERMISSION_DENY(9, "没有权限");
	private HintUtil(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	private final int code;
	private final String msg;
}
