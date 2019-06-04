package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import io.jsonwebtoken.lang.Collections;

public class ModuleCommonUtil {

	/**
	 * @param pattern yyyy MM dd HH mm ss SSS
	 */
	public static String getFormatTime(String pattern) {
		LocalDateTime dateTime = LocalDateTime.now();
		return dateTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static Random rand = new Random();

	/**
	 * @param count 随机数的个数
	 * @param min   随机数最小值
	 * @param max   随机数最大值
	 * @return 一个随机数链表[min,max)
	 */
	@SuppressWarnings("unchecked")
	public static List<Integer> randomIntegers(int count, int min, int max) {
		if (min > max)
			return null;
		IntStream ints = rand.ints(count, min, max);
		return Collections.arrayToList(ints.toArray());
	}

	public static int randInt(int bound) {
		return rand.nextInt(bound);
	}

//	public static void main(String[] args) {
//		System.out.println(randomIntegers(0, 1000, 100).size());
//	}
}
