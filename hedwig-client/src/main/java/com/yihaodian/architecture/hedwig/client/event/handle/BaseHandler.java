/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import java.util.Date;

import com.yihaodian.architecture.hedwig.client.event.BaseEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.client.util.HedwigMonitorClientUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;
import com.yihaodian.monitor.dto.ClientBizLog;
import com.yihaodian.monitor.util.MonitorConstants;
import com.yihaodian.monitor.util.MonitorJmsSendUtil;

/**
 * @author Archer
 * @param <C>
 * 
 */
public abstract class BaseHandler implements IEventHandler<HedwigContext, BaseEvent, Object> {

	@Override
	public Object handle(HedwigContext context, BaseEvent event) throws HandlerException {
		event.increaseExecCount();
		ClientBizLog cbLog = null;
		String globalId = HedwigContextUtil.getGlobalId();
		String txnId = HedwigClientUtil.generateTransactionId();
		String reqId = event.getReqestId();
		HedwigContextUtil.setTransactionId(txnId);
		cbLog = HedwigMonitorClientUtil.createClientBizLog(event, context, reqId, globalId, new Date());
		cbLog.setCommId(txnId);
		Object r = null;
		Object[] params = event.getInvocation().getArguments();
		try {
			r = doHandle(context, event, cbLog);
			event.setState(EventState.sucess);
			event.setResult(r);
			cbLog.setRespTime(new Date());
			cbLog.setSuccessed(MonitorConstants.SUCCESS);
		} catch (Throwable e) {
			event.setState(EventState.failed);
			event.setErrorMessage(e.getMessage());
			cbLog.setInParamObjects(params);
			HedwigMonitorClientUtil.setException(cbLog, e);
			if (e instanceof HandlerException) {
				throw (HandlerException) e;
			} else {
				throw new HandlerException(event.getReqestId(), e.getMessage(), e.getCause());
			}
		} finally {
			if (MonitorConstants.FAIL == cbLog.getSuccessed()) {
				cbLog.setLayerType(MonitorConstants.LAYER_TYPE_HANDLER);
				MonitorJmsSendUtil.asyncSendClientBizLog(cbLog);
			}
		}

		return r;
	}

	abstract protected Object doHandle(HedwigContext context, BaseEvent event, ClientBizLog cbLog)
			throws HandlerException;

}
