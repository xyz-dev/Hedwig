/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.HashMap;
import java.util.Map;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

/**
 * @author Archer
 *
 */
public class InvocationContext {

	private Map<String, Object> context = new HashMap<String, Object>();

	public InvocationContext() {
		super();
	}

	public String getRequestId() {
		return getStrValue(InternalConstants.HEDWIG_REQUEST_ID);
	}

	public void setRequestId(String requestId) {
		put(InternalConstants.HEDWIG_REQUEST_ID, requestId);
	}

	public String getGlobalId() {
		return getStrValue(InternalConstants.HEDWIG_GLOBAL_ID);
	}

	public void setGlobalId(String globalId) {
		put(InternalConstants.HEDWIG_REQUEST_ID, globalId);
	}

	public void put(String key, Object value) {
		if (key != null && value != null) {
			context.put(key, value);
		}
	}

	public Object getValue(String key, Object defaultObj) {
		Object value = null;
		if (key != null) {
			value = context.get(key);
		}
		value = value == null ? defaultObj : value;
		return value;
	}

	public String getStrValue(String key) {
		Object obj = getValue(key, null);
		return obj == null ? "" : (String) obj;
	}

}
