/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

/**
 * @author Archer
 * @param <T>
 * 
 */
public class BaseEvent implements IEvent<Object> {

	private static final long serialVersionUID = -3268122380332784050L;
	protected long id;
	protected String reqestId;
	protected long expireTime = InternalConstants.DEFAULT_REQUEST_TIMEOUT;
	protected TimeUnit expireTimeUnit = TimeUnit.MILLISECONDS;
	protected boolean retryable = false;
	protected int execCount = 0;
	protected int maxRedoCount = 3;
	protected long start;
	protected Object result;
	protected MethodInvocation invocation;
	protected List<EventState> states = new ArrayList<EventState>();
	protected List<String> errorMessages = new ArrayList<String>();

	public BaseEvent() {
		super();
		this.start = HedwigUtil.getCurrentTime();
	}

	@Override
	public long getStart() {
		return start;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setReqestId(String reqestId) {
		this.reqestId = reqestId;
	}

	public String getReqestId() {
		return reqestId;
	}

	@Override
	public boolean isRetryable() {
		return retryable && isExpire() && isReachMaxRedoCount();
	}

	protected boolean isExpire() {
		return (HedwigUtil.getCurrentTime() - start) > expireTime;
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
		return "BaseEvent [id=" + id + ", expireTime=" + expireTime + ", expireTimeUnit=" + expireTimeUnit + ", retryable=" + retryable
				+ ", execCount=" + execCount + ", maxRedoCount=" + maxRedoCount + ", start=" + start + ", result=" + result
				+ ", invocation=" + invocation + ", state=" + list2String(states) + ", errorMessages=" + getErrorMessages()
				+ "]";
	}

	public static String list2String(List<EventState> list) {
		StringBuilder sb = new StringBuilder("[");
		for (EventState o : list) {
			sb.append(o.toString()).append(",");
		}
		sb.append("]");
		return sb.toString();
	}
	@Override
	public EventState getState() {
		return states.get((states.size() - 1));
	}

	@Override
	public void setState(EventState state) {
		this.states.add(state);
	}

	public String getErrorMessages() {
		return HedwigUtil.list2String(errorMessages);
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessages.add(errorMessage);
	}


}
