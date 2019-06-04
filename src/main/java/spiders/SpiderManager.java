package spiders;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import utils.Manager;

@Component
public class SpiderManager {
	private static List<Spider> spiders = new ArrayList<Spider>();

	private static void init() {
		spiders.add(Manager.getBean(JSSpider.class));
		spiders.add(Manager.getBean(WXSpider.class));
		spiders.add(Manager.getBean(ZHSpider.class));
	}

	private static void startSPiders() {
		System.out.println("start");
		spiders.forEach(spider -> {
			try {
				spider.startSpider();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@PostConstruct
	void start() {
		if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), "windows")) {
			init();
			startSPiders();
		}
	}
}
