/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.exception;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;


/**
 * @author Archer Jiang
 *
 */
public class HedwigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8037986320070854409L;

	public HedwigException() {
		super();
	}

	public HedwigException(String message, Throwable cause) {
		super(InternalConstants.LOG_PROFIX + ":" + message, cause);
	}

	public HedwigException(String message) {
		super(InternalConstants.LOG_PROFIX + ":" + message);
	}

	public HedwigException(Throwable cause) {
		super(InternalConstants.LOG_PROFIX + ":" + cause.getMessage(), cause);
	}
}
