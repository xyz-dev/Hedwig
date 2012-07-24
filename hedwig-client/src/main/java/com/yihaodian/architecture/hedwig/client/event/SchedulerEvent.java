/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.concurrent.TimeUnit;

/**
 * @author Archer
 *
 */
public class SchedulerEvent extends BaseEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9165984817361999258L;
	private long delay;
	private TimeUnit delayUnit;

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public TimeUnit getDelayUnit() {
		return delayUnit;
	}

	public void setDelayUnit(TimeUnit delayUnit) {
		this.delayUnit = delayUnit;
	}

}
