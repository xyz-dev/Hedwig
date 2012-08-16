/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import java.util.Date;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
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
public abstract class BaseHandler implements IEventHandler<HedwigContext, Object> {
	protected ClientBizLog cbLog;
	@Override
	public Object handle(HedwigContext context, IEvent<Object> event) throws HandlerException {
		cbLog = new ClientBizLog();
		cbLog.setCallApp(context.getClientProfile().getClientAppName());
		cbLog.setCallHost(ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP));
		cbLog.setUniqReqId(HedwigContextUtil.getGlobalId());
		cbLog.setReqId(event.getReqestId());
		cbLog.setReqTime(new Date(event.getStart()));
		cbLog.setServiceName(context.getClientProfile().getServiceName());
		cbLog.setProviderApp(context.getClientProfile().getServiceAppName());
		Object r = null;
		Object[] params = event.getInvocation().getArguments();
		event.increaseExecCount();
		try {
			r = doHandle(context, event);
			event.setState(EventState.sucess);
			event.setResult(r);
			cbLog.setRespTime(new Date());
			cbLog.setSuccessed(MonitorConstants.SUCCESS);
		} catch (Throwable e) {
			event.setState(EventState.failed);
			event.setErrorMessage(e.getMessage());
			cbLog.setRespTime(new Date());
			cbLog.setInParamObjects(params);
			cbLog.setSuccessed(MonitorConstants.FAIL);
			cbLog.setExceptionClassname(HedwigUtil.getExceptionClassName(e));
			cbLog.setExceptionDesc(HedwigUtil.getExceptionMsg(e));
			if (e instanceof HandlerException) {
				throw (HandlerException) e;
			} else {
				throw new HandlerException(event.getReqestId(), e.getMessage());
			}
		} finally {
			try {
				MonitorJmsSendUtil.asyncSendClientBizLog(cbLog);
			} catch (Exception e2) {
			}
		}
		
		return r;
	}

	abstract protected Object doHandle(HedwigContext context, IEvent<Object> event) throws HandlerException;


}
