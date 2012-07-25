package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.event.IEventContext;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

public interface IEventHandler<C extends IEventContext, T> {

	public T handle(C eventContext, IEvent<T> event) throws HandlerException;
}
