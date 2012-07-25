/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.event.IEventContext;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

/**
 * @author Archer
 * @param <C>
 *
 */
public abstract class BaseHandler<C extends IEventContext, T> implements IEventHandler<C, T> {

	@Override
	public T handle(C context, IEvent<T> event) throws HandlerException {
		event.increaseExecCount();
		return doHandle(context, event);
	}

	abstract protected T doHandle(C context, IEvent<T> event) throws HandlerException;


}
