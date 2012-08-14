/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.engine.event.EventState;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.IEventHandler;
import com.yihaodian.monitor.dto.ClientBizLog;
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
		HedwigContextUtil.setRequestId(event.getReqestId());
		cbLog = new ClientBizLog();
		cbLog.setCallHost(ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP));
		cbLog.setUniqReqId(HedwigContextUtil.getGlobalId());
		cbLog.setReqId(event.getReqestId());
		Object r = null;
		event.increaseExecCount();
		try {
			r = doHandle(context, event);
			event.setState(EventState.sucess);
			event.setResult(r);
		} catch (Throwable e) {
			event.setState(EventState.failed);
			event.setErrorMessage(e.getMessage());
			if (e instanceof HandlerException) {
				throw (HandlerException) e;
			} else {
				throw new HandlerException(event.getReqestId(), e.getMessage());
			}
		} finally {
			HedwigContextUtil.clean();
			MonitorJmsSendUtil.asyncSendClientBizLog(cbLog);
		}
		
		return r;
	}

	abstract protected Object doHandle(HedwigContext context, IEvent<Object> event) throws HandlerException;


}
