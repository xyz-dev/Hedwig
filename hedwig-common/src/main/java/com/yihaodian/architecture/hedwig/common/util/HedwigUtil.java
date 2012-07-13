/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.List;

import com.yihaodian.architecture.hedwig.common.dto.BaseProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer Jiang
 * 
 */
public class HedwigUtil {

	public static String generateKey(BaseProfile profile) {
		StringBuilder sb = new StringBuilder(profile.getServiceAppName());
		sb.append("_").append(profile.getServiceName()).append("_")
				.append(profile.getServiceVersion());
		return sb.toString();
	}

	public static String getChildFullPath(String parentPath,
			String shortChildPath) {
		return parentPath + "/" + shortChildPath;
	}

	public static String getChildShortPath(String fullPath) {
		return fullPath.substring(fullPath.lastIndexOf("/") + 1);
	}

	public static String list2String(List<String> list) {
		StringBuilder sb = new StringBuilder("[");
		for (String o : list) {
			sb.append(o.toString()).append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public static boolean isBlankString(String value) {
		return value == null || "".equals(value);
	}

	public static String generateServiceUrl(ServiceProfile sp) {
		StringBuilder sb = new StringBuilder(sp.getProtocolPrefix());
		sb.append("://").append(sp.getHostIp()).append(":")
				.append(sp.getPort()).append("/")
				.append(sp.getServiceAppName()).append("/")
				.append(sp.getUrlPattern()).append("/")
				.append(sp.getServiceName());
		return sb.toString();
	}

	public static int ParseString2Int(String value, int defaultValue) {
		int i = defaultValue;
		try {
			i = Integer.valueOf(value);
		} catch (Exception e) {
		}
		return i;
	}

}
