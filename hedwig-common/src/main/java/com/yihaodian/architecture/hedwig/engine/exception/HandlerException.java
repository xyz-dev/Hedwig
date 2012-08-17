package com.yihaodian.architecture.hedwig.engine.exception;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;

public class HandlerException extends HedwigException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1664923907531909180L;
	protected String reqId;
	protected Throwable cause;
	public HandlerException() {
		super();
	}

	public HandlerException(String reqId, String message, Throwable cause) {
		super(createMessage(reqId, message), cause);
		this.cause = cause;
	}

	public HandlerException(String reqId, String message) {
		super(createMessage(reqId, message));
	}

	public HandlerException(String reqId, String requestId, String message) {
		super(createMessage(reqId, message));
	}

	public HandlerException(String reqId, Throwable cause) {
		super(createMessage(reqId, cause.getMessage()), cause);
		this.cause = cause;
	}

	public static String createMessage(String reqId, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(InternalConstants.HEDWIG_REQUEST_ID).append(":").append(reqId);
		sb.append(" , ").append(message).append("; ");
		return sb.toString();

	}
}
