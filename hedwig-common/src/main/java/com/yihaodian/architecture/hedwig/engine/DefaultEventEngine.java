package com.yihaodian.architecture.hedwig.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.util.HedwigExecutors;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;

public class DefaultEventEngine implements IEventEngine<Object> {

	Logger logger = LoggerFactory.getLogger(DefaultEventEngine.class);
	private static DefaultEventEngine engine = new DefaultEventEngine();

	private ExecutorService es = HedwigExecutors.newCachedThreadPool();

	public static DefaultEventEngine getEngine() {
		return engine;
	}

	private DefaultEventEngine() {
		super();
	}

	@Override
	public Object syncExecute(final IEvent<Object> event) {
		Object result = null;
		Future<Object> f = es.submit(new Runnable() {

			@Override
			public void run() {
				event.fire();
			}
		}, result);
		try {
			result = f.get(event.getExpireTime(), event.getExpireTimeUnit());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e.getCause());
		}
		return result;
	}

	@Override
	public Future<Object> asyncExecute(final IEvent<Object> event) throws EngineException {
		Object result = null;
		Future<Object> f = es.submit(new Runnable() {

			@Override
			public void run() {
				event.fire();
			}
		}, result);
		return f;

	}

	@Override
	public Object oneWayExecute(final IEvent<Object> event) throws EngineException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void schedulerExecute(final IEvent<Object> event) throws EngineException {
		// TODO Auto-generated method stub

	}

}
