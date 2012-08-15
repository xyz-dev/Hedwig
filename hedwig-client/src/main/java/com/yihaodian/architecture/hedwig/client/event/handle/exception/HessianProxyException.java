/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle.exception;

import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

/**
 * @author Archer
 *
 */
public class HessianProxyException extends HandlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1783450312917820872L;

	public HessianProxyException() {
		super();
	}

	public HessianProxyException(String reqId, String requestId, String message) {
		super(reqId, requestId, message);
	}

	public HessianProxyException(String reqId, String message, Throwable cause) {
		super(reqId, message, cause);
	}

	public HessianProxyException(String reqId, String message) {
		super(reqId, message);
	}

	public HessianProxyException(String reqId, Throwable cause) {
		super(reqId, cause);
	}

}
