package com.lotstock.eddid.route.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.cglib.beans.BeanMap;

public class BeanMapUtil {
	/**
	 * 将对象装换为map
	 * 
	 * @param bean
	 * @return
	 */
	public static <T> Map<String, Object> beanToMap(T bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				map.put(key + "", beanMap.get(key));
			}
		}
		return map;
	}

	/**
	 * 将map装换为javabean对象
	 * 
	 * @param map
	 * @param bean
	 * @return
	 */
	public static <T> T mapToBean(Map<String, Object> map, T bean) {
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}

	/**
	 * map value值降序
	 * 
	 * @param map
	 * @return
	 */
	public static List<Entry<String, Double>> sortMap(Map<String, Double> map) {
		List<Entry<String, Double>> lists = new ArrayList<Entry<String, Double>>(map.entrySet());
		Collections.sort(lists, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				double q1 = o1.getValue();
				double q2 = o2.getValue();
				double p = q2 - q1;
				if (p > 0) {
					return 1;
				} else if (p == 0) {
					return 0;
				} else
					return -1;
			}
		});
		return lists;
	}
}
