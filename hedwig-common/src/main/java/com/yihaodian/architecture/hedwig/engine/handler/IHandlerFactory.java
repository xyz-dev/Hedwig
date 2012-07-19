package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.engine.event.EventContext;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

public interface IHandlerFactory<C extends EventContext, T> {

	public IEventHandler<C, T> create(IEvent event) throws HedwigException;
}
