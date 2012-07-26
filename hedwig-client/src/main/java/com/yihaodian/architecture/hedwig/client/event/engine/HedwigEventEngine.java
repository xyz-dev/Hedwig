/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.engine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.handle.HedwigHandlerFactory;
import com.yihaodian.architecture.hedwig.client.event.util.EventUtil;
import com.yihaodian.architecture.hedwig.client.util.HedwigExecutors;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigAssert;
import com.yihaodian.architecture.hedwig.engine.IEventEngine;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.event.IScheduledEvent;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;
import com.yihaodian.architecture.hedwig.engine.handler.IHandlerFactory;

/**
 * @author Archer
 * @param <Object>
 * 
 */
public class HedwigEventEngine implements IEventEngine<HedwigContext, Object> {

	private static Logger logger = LoggerFactory.getLogger(HedwigEventEngine.class);
	private static HedwigEventEngine engine = new HedwigEventEngine();
	protected IHandlerFactory<HedwigContext, Object> handlerFactory;
	protected BlockingQueue<Runnable> eventQueue;
	protected ThreadPoolExecutor tpes;
	protected ScheduledThreadPoolExecutor stpes;

	public HedwigEventEngine() {
		super();
		this.handlerFactory = new HedwigHandlerFactory();
		this.eventQueue = new ArrayBlockingQueue<Runnable>(20);
		this.tpes = HedwigExecutors.newCachedThreadPool(eventQueue);
		this.stpes = HedwigExecutors.newSchedulerThreadPool();
	}

	@Override
	public Object syncInnerExec(HedwigContext context, final IEvent<Object> event) throws HedwigException {
		HedwigAssert.isNull(event, "Execute event must not null!!!");
		Object result = null;
		IEventHandler<HedwigContext, Object> handler = handlerFactory.create(event);
		event.setState(EventState.processing);
		result = handler.handle(context, event);
		return result;
	}

	@Override
	public Object syncPoolExec(final HedwigContext context, final IEvent<Object> event) throws HedwigException {
		HedwigAssert.isNull(event, "Execute event must not null!!!");
		Object result = null;
		Future<Object> f = null;
		try {
			final IEventHandler<HedwigContext, Object> handler = handlerFactory.create(event);
			logger.debug("Pool size:" + tpes.getPoolSize());
			f = tpes.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					Object r = null;
					try {
						event.setState(EventState.processing);
						r = handler.handle(context, event);
					} catch (Throwable e) {
						logger.debug(e.getMessage(), e);
						EventUtil.retry(handler, event, context);
					}
					return r;
				}
			});
			if (f != null) {
				result = f.get(event.getExpireTime(), event.getExpireTimeUnit());
			}
		} catch (Exception e) {
			throw new EngineException(e.getCause());
		}
		return result;
	}

	@Override
	public Future<Object> asyncExec(final HedwigContext context, final IEvent<Object> event) throws HedwigException {
		HedwigAssert.isNull(event, "Execute event must not null!!!");
		Future<Object> f = null;
		try {
			final IEventHandler<HedwigContext, Object> handler = handlerFactory.create(event);
			f = tpes.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					Object r = null;
					try {
						event.setState(EventState.processing);
						r = handler.handle(context, event);
					} catch (Throwable e) {
						logger.debug(e.getMessage());
						EventUtil.retry(handler, event, context);
					}
					return r;
				}
			});
		} catch (Exception e) {
			throw new EngineException(e.getCause());
		}
		return f;
	}

	@Override
	public void asyncReliableExec(final HedwigContext context, final IEvent<Object> event) throws HedwigException {
		throw new HedwigException("Not supported!!!");
	}

	@Override
	public Object oneWayExec(final HedwigContext context, final IEvent<Object> event) throws HedwigException {
		throw new HedwigException("Not supported!!!");
	}

	@Override
	public void schedulerExec(final HedwigContext context, final IScheduledEvent<Object> event) throws HedwigException {
		final IEventHandler<HedwigContext, Object> handler = handlerFactory.create(event);
		stpes.schedule(new Runnable() {
			
			@Override
			public void run() {
				try {
					event.setState(EventState.processing);
					handler.handle(context, event);
				} catch (HandlerException e) {
					logger.debug(e.getMessage());
				}
			}
		}, event.getDelay(), event.getDelayUnit());
	}
}
