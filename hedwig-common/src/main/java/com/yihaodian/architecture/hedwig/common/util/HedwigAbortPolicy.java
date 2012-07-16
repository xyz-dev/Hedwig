/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

/**
 * @author Archer
 *
 */
public class HedwigAbortPolicy implements RejectedExecutionHandler {

	private Logger logger = LoggerFactory.getLogger(HedwigAbortPolicy.class);
	/* (non-Javadoc)
	 * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
	 */
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		logger.debug(InternalConstants.ENGINE_LOG_PROFIX + "Hedwig Executor queue size:" + executor.getQueue().size());
		return;

	}

}
