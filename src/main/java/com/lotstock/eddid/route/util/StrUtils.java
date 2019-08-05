package com.lotstock.eddid.route.util;

import java.util.List;
import java.util.Map;

public class StrUtils {
	
	private static final String SEP1 = ",";

	public static String getParams(Map<String, String> paramValues) {
		String params = "?";
		for (String key : paramValues.keySet()) {
			String v = paramValues.get(key);
			if (v != null && v.length() != 0) {
				params += "&" + key + "=" + paramValues.get(key);
			}
		}
		return params;
	}

	/**
	 * 创建随机数
	 * 
	 * @param numberFlag
	 *            是否是纯数字
	 * @param length
	 *            生成的长度
	 * @return
	 */
	public static String createRandom(boolean numberFlag, int length) {
		String result = "";
		String strTable = numberFlag ? "1234567890" : "123456789abcdefghijklmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			result = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				result += strTable.charAt(intR);
			}
			if (count >= 1) {
				bDone = false;
			}
		} while (bDone);
		return result;
	}
	
	  /**
     * List转换String
     * 
     * @param list
     *            :需要转换的List
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                // 如果值是list类型则调用自己
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i)));
                    sb.append(SEP1);
                }  else {
                    sb.append(list.get(i));
                    sb.append(SEP1);
                }
            }
        }
        return sb.toString();
    }
	
}
