/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine;

import com.yihaodian.architecture.hedwig.engine.event.IEvent;

/**
 * Event engine is use to invoke event handler in different style such as
 * sync,async,one way, timely scheduler
 * 
 * @author Archer
 * 
 */
public interface EventEngine<T> {

	/**
	 * Invoke event handler in the caller's thread.
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public T syncExecute(IEvent event, boolean retry);

	/**
	 * Invoke event handler in thread pool
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public void asyncExecute(IEvent event, boolean retry);

	/**
	 * Invoke event handler at most on time
	 * 
	 * @param event
	 */
	public T oneWayExecute(IEvent event);

	/**
	 * Invoke event handler after a specify interval
	 * 
	 * @param event
	 * @param retry
	 */
	public void schedulerExecute(IEvent event, boolean retry);

}
