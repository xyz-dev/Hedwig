package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.event.EventContext;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

public interface IEventHandler<T, C extends EventContext> {

	public T handle(C context) throws HandlerException;
}
