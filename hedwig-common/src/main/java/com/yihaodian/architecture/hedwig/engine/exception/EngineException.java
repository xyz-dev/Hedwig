/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.exception;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

/**
 * @author Archer
 *
 */
public class EngineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3997749543314145590L;

	public EngineException() {
		super();
	}

	public EngineException(String message, Throwable cause) {
		super(InternalConstants.ENGINE_LOG_PROFIX + message, cause);
	}

	public EngineException(String message) {
		super(InternalConstants.ENGINE_LOG_PROFIX + message);
	}

	public EngineException(Throwable cause) {
		super(InternalConstants.ENGINE_LOG_PROFIX + cause.getMessage(), cause);
	}

}
