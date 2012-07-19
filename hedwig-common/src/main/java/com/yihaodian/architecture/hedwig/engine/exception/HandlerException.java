package com.yihaodian.architecture.hedwig.engine.exception;

import com.yihaodian.architecture.hedwig.common.exception.HedwigException;

public class HandlerException extends HedwigException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1664923907531909180L;

	public HandlerException() {
		super();
	}

	public HandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public HandlerException(String message) {
		super(message);
	}

	public HandlerException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
