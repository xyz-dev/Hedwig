/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.event;

import java.util.concurrent.TimeUnit;

import com.yihaodian.architecture.hedwig.engine.DefaultEventEngine;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 * @param <T>
 * 
 */
public class BaseEvent<T> implements IEvent {

	private IEventHandler<T> handler;
	private long expireTime = 2000;
	private TimeUnit expireTimeUnit = TimeUnit.MILLISECONDS;
	private boolean retryable = false;
	private int count = 0;
	private int retryCount = 3;
	private long start;
	private T result;

	public BaseEvent() {
		start = System.currentTimeMillis();
	}

	@Override
	public T fire() {
		count++;
		if (!isExpired() && count < retryCount) {
			try {
				result = handler.handle();
			} catch (HandlerException e) {
				if (retryable) {
					DefaultEventEngine.getEngine().syncExecute(this);
				}
			}
		}
		return result;
	}

	private boolean isExpired() {
		return (System.currentTimeMillis() - start) > expireTime;
	}

	public void setHandler(IEventHandler<T> handler) {
		this.handler = handler;
	}

	@Override
	public long getExpireTime() {
		return expireTime;
	}

	@Override
	public TimeUnit getExpireTimeUnit() {
		return expireTimeUnit;
	}

	@Override
	public boolean isRetryable() {
		return retryable;
	}

	@Override
	public T getResult() {
		return result;
	}

	public void setRetryable(boolean retryable) {
		this.retryable = retryable;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

}
