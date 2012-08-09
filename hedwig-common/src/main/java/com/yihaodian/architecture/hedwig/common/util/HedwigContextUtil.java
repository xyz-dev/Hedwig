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
			id = id == null ? "" : id;
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
		}
		ic.setRequestId(requestId);
	}

	public static String getGlobalId() {
		String id = "";
		try {
			InvocationContext ic = getInvocationContext();
			id = ic.getGlobalId();
			id = id == null ? "" : id;
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
		}
		ic.setRequestId(globalId);
	}

	public static void clean() {
		tl.remove();
	}
}
