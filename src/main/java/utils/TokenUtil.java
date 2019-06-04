package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenUtil {

	public static final String SECRET = "ZHUGANLINLVPIAODONGCHAOJIE";

	/** token 过期时间: 一个月 */
	public static final int calendarField = Calendar.MONTH;
	public static final int calendarInterval = 1;

	public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

	/**
	 * JWT生成Token.<br/>
	 * JWT构成: header, payload, signature
	 * 
	 * @param user_id 登录成功后用户user_id, 参数user_id不可传空
	 */
	public static String createTokenByUid(String user_id) {
		// expire time
		Calendar nowTime = Calendar.getInstance();
		Date iatDate = nowTime.getTime();
		nowTime.add(calendarField, calendarInterval);
		Date expiresDate = nowTime.getTime();
		// header Map
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");// 加密算法
		map.put("typ", "JWT");// 类型

		// build token
		// param backups {iss:Service, aud:APP}
		String token = JWT.create().withHeader(map) // header
				.withClaim("user_id", user_id).withIssuedAt(iatDate).withIssuer("ForReading").withExpiresAt(expiresDate) // expire
				.sign(ALGORITHM); // signature

		return token;
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
	}
	/**
	 * 解密Token
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static DecodedJWT verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(ALGORITHM).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
//			e.printStackTrace();
			// token 校验失败, 抛出Token验证非法异常
			return null;
		}
		return jwt;
	}

	/**
	 * 根据Token获取user_id
	 * 
	 * @param token
	 * @return user_id
	 */
	public static String getUserIdByToken(String token) {
		try {
			DecodedJWT jwt = verifyToken(token);
			Claim user_id_claim = jwt.getClaim("user_id");
			if (StringUtils.isEmpty(user_id_claim.asString())) {
				return null;
			}
			return user_id_claim.asString();
		} catch (Exception e) {
			return null;
		}
	}

//	public static void main(String[] args) throws InterruptedException {
//		String token = createTokenByUid("20161060137");
//		Thread.sleep(6000);
//		DecodedJWT jwt = verifyToken(token);
//		if (jwt == null)
//			System.out.println("无效的token");
//		jwt.getClaims().forEach((x, y) -> System.out.println(x + " " + y.asString()));
//	}
}