/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import org.aopalliance.intercept.MethodInvocation;

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

	public IEventHandler<HedwigContext, Object, MethodInvocation> create(String handlerName) {
		return null;

	}
	
}
