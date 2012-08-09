/**
 * 
 */
package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
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
		HedwigContextUtil.setRequestId(event.getReqestId());
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
				throw new HandlerException(event.getReqestId(), e.getMessage());
			}
		} finally {
			HedwigContextUtil.clean();
		}
		
		return r;
	}

	abstract protected T doHandle(C context, IEvent<T> event) throws HandlerException;


}
