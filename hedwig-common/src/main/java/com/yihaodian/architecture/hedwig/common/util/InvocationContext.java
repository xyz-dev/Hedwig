/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Archer
 *
 */
public class InvocationContext {

	private Map<String, Object> context = new HashMap<String, Object>();
	private String requestId;
	private String globalId;
	private Object[] args;

	public InvocationContext() {
		super();
	}


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
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
