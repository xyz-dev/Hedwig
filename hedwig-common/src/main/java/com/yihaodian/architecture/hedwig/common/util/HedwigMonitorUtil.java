/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.Gson;
import com.yihaodian.architecture.hedwig.common.bean.ExecutorInfo;

/**
 * @author Archer
 *
 */
public class HedwigMonitorUtil {
	public static Gson gson = new Gson();

	public static String getExceptionClassName(Throwable throwable) {
		String name = "";
		if (throwable != null) {
			Throwable t = throwable;
			Throwable cause = throwable.getCause();
			while (cause != null) {
				t = cause;
				cause = cause.getCause();
			}
			name = t.getClass().getName();
		}
		return name;
	}

	public static String getExceptionMsg(Throwable throwable) {
		StringBuilder msg = new StringBuilder(throwable.getClass().getName());
		msg.append(": ");
		if (throwable != null) {
			Throwable cause = throwable;
			while (cause != null) {
				msg.append(cause.getMessage()).append(" ");
				cause = cause.getCause();
			}
		}
		return msg.toString();
	}

	public static String getThreadPoolInfo(ThreadPoolExecutor tpes) {
		return gson.toJson(new ExecutorInfo(tpes));
	}

}
