/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine;

import java.util.concurrent.Future;

import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;

/**
 * Event engine is use to invoke event handler in different style such as
 * sync,async,one way, timely scheduler
 * 
 * @author Archer
 * 
 */
public interface IEventEngine<T, I> {

	/**
	 * Invoke event handler in the caller's thread.
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public T syncExecute(IEvent<T, I> event) throws EngineException;

	/**
	 * Invoke event handler in thread pool
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public Future<Object> asyncExecute(IEvent<T, I> event) throws EngineException;

	/**
	 * Invoke event handler at most on time
	 * 
	 * @param event
	 */
	public T oneWayExecute(IEvent<T, I> event) throws EngineException;

	/**
	 * Invoke event handler after a specify interval
	 * 
	 * @param event
	 * @param retry
	 */
	public void schedulerExecute(IEvent<T, I> event) throws EngineException;

}
