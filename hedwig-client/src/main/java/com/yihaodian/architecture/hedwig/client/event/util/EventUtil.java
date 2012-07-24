/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 *
 */
public class EventUtil {

	public static Logger logger = LoggerFactory.getLogger(EventUtil.class);

	public static Object retry(IEventHandler<HedwigContext, Object> handler, IEvent<HedwigContext, Object> event) throws HedwigException {
		while (event.isRetryable()) {
			try {
				return handler.handle(event.getContext(), event);
			} catch (Throwable e) {
				logger.debug(e.getMessage());
			}
		}
		throw new HedwigException("Exec " + event.getExecCount() + " times,event " + event.toString() + " had been retry enough!!!");
	}
}
