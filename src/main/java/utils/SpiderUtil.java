package utils;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SpiderUtil {
	public static Document getDocumentByUrl(String aimUrl, Map<String, String> paraMap) throws IOException {
		Connection conn = Jsoup.connect(aimUrl);
		if (paraMap != null)
			conn.data(paraMap);
		return conn.get();
	}

	public static Timer getTimer() {
		return new Timer();
	}
}
