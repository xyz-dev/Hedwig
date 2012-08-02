/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

/**
 * @author Archer
 *
 */
public class RequestContext {

	private String requestId;

	public RequestContext(String requestId) {
		super();
		this.requestId = requestId;
	}

	public RequestContext() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
