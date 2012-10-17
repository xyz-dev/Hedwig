/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.common.dto.BaseProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer Jiang
 * 
 */
public class HedwigUtil {

	public static String generateKey(BaseProfile profile) {
		StringBuilder sb = new StringBuilder(profile.getServiceAppName());
		sb.append("_").append(profile.getServiceName()).append("_").append(profile.getServiceVersion());
		return sb.toString();
	}

	public static String getChildFullPath(String parentPath, String shortChildPath) {
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
		sb.append("://").append(sp.getHostIp()).append(":").append(sp.getPort()).append("/");
		if(sp.isAssembleAppName()){
			sb.append(sp.getServiceAppName());
		}
		sb.append(sp.getUrlPattern()).append("/").append(sp.getServiceName());
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

	public static String generateHandlerName(Class clz) {
		return clz.getName();
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static long getCurrentNanoTime() {
		return System.nanoTime();
	}

	public static String getHostFromUrl(String url) {
		String value = "";
		if (!isBlankString(url)) {
			String[] arr = url.split("/", 6);
			value = arr[2];
		}
		return value;
	}

	public static String getMethodName(MethodInvocation invocation) {
		String value = "unknowMethod";
		if (invocation != null) {
			value = invocation.getMethod().getName();
		}
		return value;
	}

	public static String getClassName(MethodInvocation invocation) {
		String value = "unKnowClass";
		if (invocation != null) {
			value = getShortClassName(invocation.getMethod().getDeclaringClass().getName());
		}
		return value;
	}

	public static String getShortClassName(String clazzName){
		String value = clazzName;
		if(clazzName!=null){
			String[] arr = clazzName.split("\\.");
			if (arr != null && arr.length > 0) {
				value = arr[(arr.length - 1)];
			}
		}
		return value;
	}

	public static String getErrorMsg(Throwable ex) {
		String value = "";
		if (ex != null) {
			value = ex.getMessage();
			if (isBlankString(value)) {
				value = ex.getClass().getName();
			}
		}
		return value;
	}
}
