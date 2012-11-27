/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.engine;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.client.event.BaseEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.event.handle.HandlerUtil;
import com.yihaodian.architecture.hedwig.client.event.handle.HedwigHandlerFactory;
import com.yihaodian.architecture.hedwig.client.event.util.EngineUtil;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.client.util.HedwigMonitorClientUtil;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.RequestType;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigAssert;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigExecutors;
import com.yihaodian.architecture.hedwig.common.util.HedwigMonitorUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.IEventEngine;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.exception.EngineException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;
import com.yihaodian.architecture.hedwig.engine.handler.IHandlerFactory;
import com.yihaodian.monitor.dto.ClientBizLog;
import com.yihaodian.monitor.util.MonitorConstants;
import com.yihaodian.monitor.util.MonitorJmsSendUtil;

/**
 * @author Archer
 * @param <Object>
 * 
 */
public class HedwigEventEngine implements IEventEngine<HedwigContext, BaseEvent, Object> {

	private static Logger logger = LoggerFactory.getLogger(HedwigEventEngine.class);
	private static HedwigEventEngine engine = new HedwigEventEngine();
	protected IHandlerFactory<HedwigContext, BaseEvent, Object> handlerFactory;
	protected BlockingQueue<Runnable> eventQueue;
	protected ThreadPoolExecutor tpes;
	protected ScheduledThreadPoolExecutor stpes;

	public static HedwigEventEngine getEngine() {
		return engine;
	}

	private HedwigEventEngine() {
		super();
		this.handlerFactory = new HedwigHandlerFactory();
		this.tpes = HedwigExecutors.newCachedThreadPool(InternalConstants.HEDWIG_CLIENT);
		this.stpes = HedwigExecutors.newSchedulerThreadPool(InternalConstants.HEDWIG_CLIENT);
	}

	@Override
	public Object syncInnerThreadExec(HedwigContext context, final BaseEvent event) {
		HedwigAssert.isNull(event, "Execute event must not null!!!");
		Object result = null;
		Date reqTime = new Date(event.getStart());
		final String globalId = getGlobalId(event);
		final String reqId = event.getReqestId();
		final ClientBizLog cbLog = HedwigMonitorClientUtil.createClientBizLog(event, context, reqId, globalId, reqTime);
		HedwigContextUtil.setGlobalId(globalId);
		HedwigContextUtil.setRequestId(reqId);
		HedwigContextUtil.setAttribute(InternalConstants.HEDWIG_INVOKE_TIME, reqTime);
		Object[] params = event.getInvocation().getArguments();
		IEventHandler<HedwigContext, BaseEvent, Object> handler = handlerFactory.create(event);
		event.setState(EventState.processing);
		try {
			result = handler.handle(context, event);
			cbLog.setProviderHost(HedwigContextUtil.getString(InternalConstants.HEDWIG_SERVICE_IP, ""));
			cbLog.setRespTime(new Date());
			cbLog.setSuccessed(MonitorConstants.SUCCESS);
		} catch (Throwable e) {
			cbLog.setInParamObjects(params);
			HedwigMonitorClientUtil.setException(cbLog, e);
		} finally {
			cbLog.setLayerType(MonitorConstants.LAYER_TYPE_ENGINE);
			MonitorJmsSendUtil.asyncSendClientBizLog(cbLog);
			HedwigContextUtil.clean();
		}

		return result;
	}

	@Override
	public Object syncPoolExec(final HedwigContext context, final BaseEvent event) {
		HedwigAssert.isNull(event, "Execute event must not null!!!");
		Object result = null;
		Future<Object> f = null;
		final Date reqTime = new Date(event.getStart());
		final String globalId = getGlobalId(event);
		final String reqId = event.getReqestId();
		final ClientBizLog cbLog = HedwigMonitorClientUtil.createClientBizLog(event, context, reqId, globalId, reqTime);
		Object[] params = event.getInvocation().getArguments();
		try {
			final IEventHandler<HedwigContext, BaseEvent, Object> handler = handlerFactory.create(event);
			cbLog.setMemo(HedwigMonitorUtil.getThreadPoolInfo(tpes));
			f = tpes.submit(new Callable<Object>() {

				@Override
				public Object call() throws EngineException {
					Object r = null;
					try {
						HedwigContextUtil.setGlobalId(globalId);
						HedwigContextUtil.setRequestId(reqId);
						HedwigContextUtil.setAttribute(InternalConstants.HEDWIG_INVOKE_TIME, reqTime);
						event.setState(EventState.processing);
						r = handler.handle(context, event);
					} catch (Throwable e) {
						if (HandlerUtil.isNetworkException(e)) {
							r = EngineUtil.retry(handler, event, context);
						}
					} finally {
						cbLog.setProviderHost(HedwigContextUtil.getString(InternalConstants.HEDWIG_SERVICE_IP, ""));
						cbLog.setCommId(HedwigContextUtil.getTransactionId());
						HedwigContextUtil.clean();
					}
					return r;
				}
			});
			result = f.get(event.getExpireTime(), event.getExpireTimeUnit());
			cbLog.setRespTime(new Date());
			if (event.getState().equals(EventState.sucess)) {
				cbLog.setSuccessed(MonitorConstants.SUCCESS);
			} else {
				cbLog.setSuccessed(MonitorConstants.FAIL);
				throw new EngineException(cbLog.getServiceMethodName() + " execute failed:" + event.getErrorMessages()
						+ " use \"reqId\" to query the detail message!!!");
			}
		} catch (Throwable e) {
			cbLog.setInParamObjects(params);
			HedwigMonitorClientUtil.setException(cbLog, e);
		} finally {
			cbLog.setLayerType(MonitorConstants.LAYER_TYPE_ENGINE);
			MonitorJmsSendUtil.asyncSendClientBizLog(cbLog);
		}
		return result;
	}

	private String getGlobalId(BaseEvent event) {
		String globalId = HedwigContextUtil.getGlobalId();
		if (HedwigUtil.isBlankString(globalId)) {
			globalId = HedwigClientUtil.generateGlobalId(event);
		}
		return globalId;
	}

	@Override
	public Future<Object> asyncExec(final HedwigContext context, final BaseEvent event) throws HedwigException {
		throw new HedwigException("Not supported yet!!!");
	}

	@Override
	public void asyncReliableExec(final HedwigContext context, final BaseEvent event) throws HedwigException {
		throw new HedwigException("Not supported yet!!!");
	}

	@Override
	public Object oneWayExec(final HedwigContext context, final BaseEvent event) throws HedwigException {
		throw new HedwigException("Not supported yet!!!");
	}

	@Override
	public void schedulerExec(final HedwigContext context, final BaseEvent event) throws HedwigException {
		final IEventHandler<HedwigContext, BaseEvent, Object> handler = handlerFactory.create(event);
		final String globalId = getGlobalId(event);
		stpes.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					HedwigContextUtil.setGlobalId(globalId);
					HedwigContextUtil.setRequestId(event.getReqestId());
					event.setState(EventState.processing);
					handler.handle(context, event);
				} catch (Throwable e) {
					logger.debug(e.getMessage());
				} finally {
					HedwigContextUtil.clean();
				}
			}
		}, event.getDelay(), event.getDelayUnit());
	}

	public Object exec(HedwigContext eventContext, BaseEvent event) {
		BaseEvent bevent = event;
		int type = bevent.getRequestType().getIndex();
		Object result = null;
		if (RequestType.SyncInner.getIndex() == type) {
			result = syncInnerThreadExec(eventContext, event);
		} else {
			result = syncPoolExec(eventContext, event);
		}
		return result;
	}

	@Override
	public void shutdown() {
		tpes.shutdown();
		stpes.shutdown();
	}
}
