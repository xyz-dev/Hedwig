package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.event.EventContext;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

public interface IEventHandler<C extends EventContext, T, I> {

	public T handle(C context, IEvent<T, I> event) throws HandlerException;
}
