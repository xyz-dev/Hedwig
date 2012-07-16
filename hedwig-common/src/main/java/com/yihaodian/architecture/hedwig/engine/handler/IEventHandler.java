package com.yihaodian.architecture.hedwig.engine.handler;

import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;

public interface IEventHandler<T> {

	public T handle() throws HandlerException;
}
