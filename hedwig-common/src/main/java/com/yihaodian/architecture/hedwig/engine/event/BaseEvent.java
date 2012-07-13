/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.event;

import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 * @param <T>
 * 
 */
public class BaseEvent<T> implements IEvent {

	private IEventHandler<T> handler;
	private boolean retry;

	@Override
	public void fire() {
		handler.handle();
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}

	public void setHandler(IEventHandler<T> handler) {
		this.handler = handler;
	}

}
