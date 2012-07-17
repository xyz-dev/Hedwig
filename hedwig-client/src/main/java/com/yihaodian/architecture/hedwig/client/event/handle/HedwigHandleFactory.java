/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 *
 */
public class HedwigHandleFactory {

	public static HedwigHandleFactory factory = new HedwigHandleFactory();

	public static HedwigHandleFactory getinstance() {
		return factory;
	}

	private HedwigHandleFactory() {
		super();
	}

	public IEventHandler<Object, HedwigContext> create(String handlerName) {
		return null;

	}
	
}
