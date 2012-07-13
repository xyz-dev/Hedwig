package com.yihaodian.architecture.hedwig.engine;

import java.util.concurrent.ExecutorService;

import com.yihaodian.architecture.hedwig.common.util.HedwigExecutors;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;

public class DefaultEventEngine implements IEventEngine<Object> {
	ExecutorService es = HedwigExecutors.newCachedThreadPool();

	@Override
	public Object syncExecute(IEvent event) throws EngineException {
		return null;
	}

	@Override
	public void asyncExecute(IEvent event) throws EngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object oneWayExecute(IEvent event) throws EngineException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void schedulerExecute(IEvent event) throws EngineException {
		// TODO Auto-generated method stub

	}

}
