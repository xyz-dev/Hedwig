package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.event.IEventContext;

public interface IHandlerFactory<C extends IEventContext, T> {

	public IEventHandler<C, T> create(IEvent event) throws HedwigException;
}
