/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.event.EventState;
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
		T r=null;
		event.increaseExecCount();
		try {
			r = doHandle(context, event);
			event.setState(EventState.sucess);
			event.setResult(r);
		} catch (Throwable e) {
			event.setState(EventState.failed);
			event.setErrorMessage(e.getMessage());
			if (e instanceof HandlerException) {
				throw (HandlerException) e;
			} else {
				throw new HandlerException(e.getMessage());
			}
		}
		
		return r;
	}

	abstract protected T doHandle(C context, IEvent<T> event) throws HandlerException;


}
