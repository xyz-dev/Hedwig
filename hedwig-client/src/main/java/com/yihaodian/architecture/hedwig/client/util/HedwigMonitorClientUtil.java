/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.util.Date;

import com.yihaodian.architecture.hedwig.client.event.BaseEvent;
import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigMonitorUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.monitor.dto.ClientBizLog;
import com.yihaodian.monitor.util.MonitorConstants;

/**
 * @author Archer
 *
 */
public class HedwigMonitorClientUtil {

	public static ClientBizLog createClientBizLog(IEvent<Object> event, HedwigContext context, String reqId,
			String globalId, Date reqTime) {
		BaseEvent be = (BaseEvent) event;
		ClientBizLog cbLog = new ClientBizLog();
		cbLog.setCallApp(context.getClientProfile().getClientAppName());
		cbLog.setCallHost(ProperitesContainer.client().getProperty(PropKeyConstants.HOST_IP));
		cbLog.setUniqReqId(globalId);
		cbLog.setServiceName(context.getClientProfile().getServiceName());
		cbLog.setProviderApp(context.getClientProfile().getServiceAppName());
		cbLog.setReqId(reqId);
		cbLog.setReqTime(reqTime);
		cbLog.setMethodName(be.getCallerMethod());
		cbLog.setServiceMethodName(be.getServiceMethod());
		return cbLog;
	}

	public static void setException(ClientBizLog cbLog, Throwable exception) {
		cbLog.setRespTime(new Date());
		cbLog.setSuccessed(MonitorConstants.FAIL);
		cbLog.setExceptionClassname(HedwigMonitorUtil.getExceptionClassName(exception));
		cbLog.setExceptionDesc(HedwigMonitorUtil.getExceptionMsg(exception));
	}

}
