/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine;

import java.util.concurrent.Future;

import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.event.IEventContext;
import com.yihaodian.architecture.hedwig.engine.event.IScheduledEvent;

/**
 * Event engine is use to invoke event handler in different style such as
 * sync,async,one way, timely scheduler
 * 
 * @author Archer
 * 
 */
public interface IEventEngine<C extends IEventContext, T> {

	/**
	 * Invoke event handler in the caller's thread,can't retry.
	 * 
	 * @param event
	 * @param retry
	 * @return
	 * @throws Exception
	 */
	public T syncInnerExec(C context, final IEvent<T> event) throws HedwigException;

	/**
	 * Invoke event handler in thread pool
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public T syncPoolExec(C context, final IEvent<T> event) throws HedwigException;

	/**
	 * Invoke event handler in thread pool
	 * 
	 * @param event
	 * @param retry
	 * @return
	 */
	public Future<T> asyncExec(C context, final IEvent<T> event) throws HedwigException;

	/**
	 * Reliable asynchronous request executor,base on message server
	 * 
	 * @param event
	 * @throws HedwigException
	 */
	public void asyncReliableExec(C context, final IEvent<T> event) throws HedwigException;
	/**
	 * Invoke event handler at most on time
	 * 
	 * @param event
	 */
	public T oneWayExec(C context, final IEvent<T> event) throws HedwigException;

	/**
	 * Invoke event handler after a specify interval
	 * 
	 * @param event
	 * @param retry
	 */
	public void schedulerExec(C context, final IScheduledEvent<T> event) throws HedwigException;

}
