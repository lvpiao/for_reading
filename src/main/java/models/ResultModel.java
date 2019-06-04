package models;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultModel extends JSONObject {

	private static final long serialVersionUID = 7248437805065745558L;

	public ResultModel(int type) {
		switch (type) {
		case JR.SUCCESS:
			this.put(JR.K_RESULT, JR.V_SUCCESS);
			break;
		case JR.FAIL:
			this.put(JR.K_RESULT, JR.V_FAIL);
			break;
		default:
			break;
		}
	}

	public ResultModel(int type, JSONArray data) {
		switch (type) {
		case JR.SUCCESS:
			this.put(JR.K_RESULT, JR.V_SUCCESS);
			setData(data);
			break;
		case JR.FAIL:
			this.put(JR.K_RESULT, JR.V_FAIL);
			setData(data);
			break;
		default:
			break;
		}
	}

	public ResultModel(int type, List<Map<String, String>> data) {
		switch (type) {
		case JR.SUCCESS:
			this.put(JR.K_RESULT, JR.V_SUCCESS);
			setData(data);
			break;
		case JR.FAIL:
			this.put(JR.K_RESULT, JR.V_FAIL);
			setData(data);
			break;
		default:
			break;
		}
	}

	public ResultModel(int type, String msg) {
		switch (type) {
		case JR.SUCCESS:
			this.put(JR.K_RESULT, JR.V_SUCCESS);
			setMessage(msg);
			break;
		case JR.FAIL:
			this.put(JR.K_RESULT, JR.V_FAIL);
			setMessage(msg);
			break;
		default:
			break;
		}
	}

	public void setData(JSONArray data) {
		put(JR.K_DATA, data);
	}

	public void setData(List<Map<String, String>> data) {
		put(JR.K_DATA, data);
	}

	public void setData(String data) {
		put(JR.K_DATA, JSON.parseArray(data));
	}

	public JSONArray getData() {
		return getJSONArray(JR.K_DATA);
	}

	public String getDataAsText() {
		return getJSONArray(JR.K_DATA).toJSONString();
	}

	public void setMessage(String msg) {
		put(JR.K_MSG, msg);
	}

	public String getMessage() {
		return getString(JR.K_MSG);
	}

	public void setResultCode(int code) {
		put(JR.K_CODE, code);
	}

	public void setToken(String token) {
		put(JR.K_TOKEN, token);
	}

	public String getToken() {
		return getString(JR.K_TOKEN);
	}
}
