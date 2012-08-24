/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;


/**
 * @author Archer
 * 
 */
public class HedwigContextUtil {
	private static ThreadLocal<InvocationContext> tl = new ThreadLocal<InvocationContext>();

	public static InvocationContext getInvocationContext() {
		InvocationContext ic = tl.get();
		if (ic == null) {
			ic = new InvocationContext();
			tl.set(ic);
		}
		return ic;
	}

	public static String getRequestId() {
		InvocationContext ic = getInvocationContext();
		return ic.getRequestId();

	}

	public static boolean isVoidMethod() {
		return getInvocationContext().isVoidMethod();
	}

	public static void setVoidMethod(boolean b) {
		getInvocationContext().setVoidMethod(b);
	}

	public static void setArguments(Object[] params) {
		if (params == null)
			return;
		getInvocationContext().setArgs(params);
	}

	public static Object[] getArguments() {
		return getInvocationContext().getArgs();
	}

	public static void setRequestId(String requestId) {
		if (!HedwigUtil.isBlankString(requestId)) {
			getInvocationContext().setRequestId(requestId);
		}
	}

	public static String getGlobalId() {
		return getInvocationContext().getGlobalId();
	}

	public static void setGlobalId(String globalId) {
		if (!HedwigUtil.isBlankString(globalId)) {
			getInvocationContext().setGlobalId(globalId);
		}
	}

	public static void setTransactionId(String txnId) {
		if (!HedwigUtil.isBlankString(txnId)) {
			getInvocationContext().setTransactionId(txnId);
		}
	}

	public static String getTransactionId() {
		return getInvocationContext().getTransactionId();
	}

	public static void setAttribute(String key, Object value) {
		if (InternalConstants.HEDWIG_REQUEST_ID.equals(key)) {
			setRequestId((String) value);
		} else if (InternalConstants.HEDWIG_GLOBAL_ID.equals(key)) {
			setGlobalId((String) value);
		} else if (InternalConstants.HEDWIG_TXN_ID.equals(key)) {
			setTransactionId((String) value);
		}
		getInvocationContext().put(key, value);
	}

	public static Object getAttribute(String key, Object defValue) {
		Object value = defValue;
		try {
			value = getInvocationContext().getValue(key, defValue);
		} catch (Exception e) {
		}
		return value;
	}

	public static String getString(String key, String defValue) {
		return (String) getAttribute(key, "");
	}

	public static void clean() {
		tl.remove();
	}
}
