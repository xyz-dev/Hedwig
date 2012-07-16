package com.yihaodian.architecture.hedwig.engine.exception;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

public class HandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1664923907531909180L;

	public HandlerException() {
		super();
	}

	public HandlerException(String message, Throwable cause) {
		super(InternalConstants.HANDLE_LOG_PROFIX + message, cause);
	}

	public HandlerException(String message) {
		super(InternalConstants.HANDLE_LOG_PROFIX + message);
	}

	public HandlerException(Throwable cause) {
		super(InternalConstants.HANDLE_LOG_PROFIX + cause.getMessage(), cause);
	}

}
