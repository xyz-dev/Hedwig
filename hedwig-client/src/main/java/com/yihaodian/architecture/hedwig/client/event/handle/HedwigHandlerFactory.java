/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import java.util.HashMap;
import java.util.Map;

import com.yihaodian.architecture.hedwig.client.event.DirectRequestEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.SyncRequestEvent;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigAssert;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;
import com.yihaodian.architecture.hedwig.engine.handler.IHandlerFactory;

/**
 * @author Archer
 *
 */
public class HedwigHandlerFactory implements IHandlerFactory<HedwigContext, Object> {

	public Map<String, IEventHandler<HedwigContext, Object>> handlerMap = new HashMap<String, IEventHandler<HedwigContext, Object>>();

	public HedwigHandlerFactory() {
		handlerMap.put(HedwigUtil.generateHandlerName(SyncRequestEvent.class), new SyncRequestHandler());
		handlerMap.put(HedwigUtil.generateHandlerName(DirectRequestEvent.class), new DirectRequestHandler());
	}

	@Override
	public IEventHandler<HedwigContext, Object> create(IEvent event) throws HedwigException {
		IEventHandler<HedwigContext, Object> handler = null;
		String hName = HedwigUtil.generateHandlerName(event.getClass());
		if (handlerMap.size() > 0 && handlerMap.containsKey(hName)) {
			handler = handlerMap.get(hName);
		}
		HedwigAssert.isNull(handler, hName + " is not proper initialized!!!");
		return handler;
	}


	
}
