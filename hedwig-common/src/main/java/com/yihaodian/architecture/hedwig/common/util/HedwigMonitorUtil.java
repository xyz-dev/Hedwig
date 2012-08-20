/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

/**
 * @author Archer
 *
 */
public class HedwigMonitorUtil {
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
		StringBuilder msg = new StringBuilder();
		if (throwable != null) {
			Throwable cause = throwable;
			while (cause != null) {
				msg.append(cause.getMessage());
				cause = cause.getCause();
			}
		}
		return msg.toString();
	}

}
