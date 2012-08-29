/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 *
 */
public class EngineUtil {

	public static Logger logger = LoggerFactory.getLogger(EngineUtil.class);

	public static Object retry(IEventHandler<HedwigContext, Object> handler, IEvent<Object> event, HedwigContext context)
			throws HedwigException {
		while (event.isRetryable()) {
			try {
				return handler.handle(context, event);
			} catch (Throwable e) {
				logger.debug(e.getMessage());
			}
		}
		throw new EngineException("Exec " + event.getExecCount() + " times,event " + event.toString()
				+ " had been retry enough!!!");
	}
}
