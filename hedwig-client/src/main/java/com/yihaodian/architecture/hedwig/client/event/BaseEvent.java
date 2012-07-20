/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

/**
 * @author Archer
 * @param <T>
 * 
 */
public class BaseEvent implements IEvent<HedwigContext, Object> {

	private static final long serialVersionUID = -3268122380332784050L;
	protected long expireTime = InternalConstants.DEFAULT_REQUEST_TIMEOUT;
	protected TimeUnit expireTimeUnit = TimeUnit.MILLISECONDS;
	protected boolean retryable = false;
	protected int execCount = 0;
	protected int maxRedoCount = 3;
	protected long start;
	protected Object result;
	protected HedwigContext context;
	protected MethodInvocation invocation;

	@Override
	public HedwigContext getContext() {
		return context;
	}

	@Override
	public boolean isRetryable() {
		return retryable && isExpire() && isReachMaxRedoCount();
	}

	protected boolean isExpire() {
		return (System.currentTimeMillis() - start) > expireTime;
	}

	protected boolean isReachMaxRedoCount() {
		return execCount <= maxRedoCount;
	}

	@Override
	public void increaseExecCount() {
		execCount++;
	}
	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public MethodInvocation getInvocation() {
		return invocation;
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
	public void setResult(Object result) {
		this.result = result;

	}

	public int getMaxRedoCount() {
		return maxRedoCount;
	}

	public void setMaxRedoCount(int maxRedoCount) {
		this.maxRedoCount = maxRedoCount;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public void setExpireTimeUnit(TimeUnit expireTimeUnit) {
		this.expireTimeUnit = expireTimeUnit;
	}

	public void setRetryable(boolean retryable) {
		this.retryable = retryable;
	}

	@Override
	public int getExecCount() {
		return execCount;
	}

	@Override
	public String toString() {
		return "BaseEvent [expireTime=" + expireTime + ", expireTimeUnit=" + expireTimeUnit + ", retryable=" + retryable + ", execCount="
				+ execCount + ", maxRedoCount=" + maxRedoCount + ", start=" + start + ", result=" + result + ", context=" + context
				+ ", invocation=" + invocation + "]";
	}
}
