/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

/**
 * @author Archer
 *
 */
public class InvocationContext {

	private String globalId;
	private String requestId;

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

}
