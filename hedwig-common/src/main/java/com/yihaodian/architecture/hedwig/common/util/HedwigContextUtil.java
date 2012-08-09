/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidReturnValueException;

/**
 * @author Archer
 *
 */
public class HedwigContextUtil {
	private static ThreadLocal<InvocationContext> tl = new ThreadLocal<InvocationContext>();

	public static InvocationContext getInvocationContext() throws HedwigException {
		InvocationContext ic = tl.get();
		if (ic == null) {
			throw new InvalidReturnValueException(InternalConstants.LOG_PROFIX + "No context exist in current thread"
					+ Thread.currentThread().getName());
		}
		return ic;
	}

	public static String getRequestId() {
		String id = "";
		try {
			InvocationContext ic = getInvocationContext();
			id = ic.getRequestId();
		} catch (Exception e) {
		}
		return id;
	}

	public static void setRequestId(String requestId) {
		if(requestId==null) return;
		InvocationContext ic = null;
		try {
			ic = getInvocationContext();
		} catch (Exception e) {
			ic = new InvocationContext();
			tl.set(ic);
		}
		ic.setRequestId(requestId);
	}

	public static String getGlobalId() {
		String id = "";
		try {
			InvocationContext ic = getInvocationContext();
			id = ic.getGlobalId();
		} catch (Exception e) {
		}
		return id;
	}

	public static void setGlobalId(String globalId) {
		if(globalId==null) return;
		InvocationContext ic = null;
		try {
			ic = getInvocationContext();
		} catch (Exception e) {
			ic = new InvocationContext();
			tl.set(ic);
		}
		ic.setGlobalId(globalId);
	}

	public static void setAttribute(String key, Object value) {
		InvocationContext ic = null;
		try {
			ic = getInvocationContext();
		} catch (Exception e) {
			ic = new InvocationContext();
			tl.set(ic);
		}
		ic.put(key, value);
	}
	
	public static Object getAttribute(String key, Object defValue) {
		Object value = defValue;
		InvocationContext ic = null;
		try {
			ic = getInvocationContext();
			value = ic.getValue(key, defValue);
		} catch (Exception e) {
		}
		return value;
	}

	public static void clean() {
		tl.remove();
	}
}
