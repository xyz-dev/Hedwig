/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.event.engine.DefaultEventEngine;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;

/**
 * @author Archer
 * @param <T>
 * 
 */
public class BaseEvent implements IEvent<Object, MethodInvocation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3268122380332784050L;
	private IEventHandler<HedwigContext, Object, MethodInvocation> handler;
	private long expireTime = InternalConstants.DEFAULT_REQUEST_TIMEOUT;
	private TimeUnit expireTimeUnit = TimeUnit.MILLISECONDS;
	private boolean retryable = false;
	private int count = 0;
	private int retryCount = 3;
	private long start;
	private Object result;
	private HedwigContext context;
	private MethodInvocation invocation;


	public BaseEvent(HedwigContext context) {
		this.retryCount = context.getLocator().getAllService().size();
		retryable = this.retryCount > 1 ? true : false;
		this.context = context;
		this.start = System.currentTimeMillis();
		this.expireTime = context.getClientProfile().getTimeout() * 200;
	}

	@Override
	public Object fire() {
		count++;
		if (!isExpired() && count <= retryCount) {
			try {
				result = handler.handle(context, this);
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

	public void setHandler(IEventHandler<HedwigContext, Object, MethodInvocation> handler) {
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
	public Object getResult() {
		return result;
	}

	public void setRetryable(boolean retryable) {
		this.retryable = retryable;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public MethodInvocation getInvocation() {
		return invocation;
	}

	public void setInvocation(MethodInvocation invocation) {
		this.invocation = invocation;
	}

}
