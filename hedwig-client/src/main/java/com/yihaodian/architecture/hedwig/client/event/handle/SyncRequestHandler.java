/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.monitor.util.MonitorConstants;

/**
 * @author Archer
 * 
 */
public class SyncRequestHandler extends BaseHandler {

	@Override
	public Object doHandle(HedwigContext context, IEvent<Object> event) throws HandlerException {

		Object result = null;
		ServiceProfile sp = context.getLocator().getService();
		String reqId = event.getReqestId();
		Object hessianProxy = null;
		if (sp == null)
			throw new HandlerException(reqId, "Can't find service provider for :" + context.getClientProfile().toString());
		String sUrl = sp.getServiceUrl();
		try {
			hessianProxy = HedwigClientUtil.getHessianProxy(context, sUrl);
		} catch (Exception e) {
			throw new HandlerException(reqId, e.getCause());
		}

		if (hessianProxy == null) {
			sp.setAvailable(false);
			context.getHessianProxyMap().remove(sUrl);
			throw new HandlerException(reqId, "HedwigHessianInterceptor is not properly initialized");
		}
		MethodInvocation invocation = event.getInvocation();
		Object[] params = invocation.getArguments();
		try {
			cbLog.setProviderApp(sp.getServiceAppName());
			cbLog.setProviderHost(sp.getHostIp());
			result = invocation.getMethod().invoke(hessianProxy, params);
			cbLog.setRespTime(new Date());
			cbLog.setSuccessed(MonitorConstants.SUCCESS);
		} catch (Throwable e) {
			cbLog.setInParamObjects(params);
			cbLog.setSuccessed(MonitorConstants.FAIL);
			cbLog.setExceptionClassname(e.getCause().getClass().getName());
			cbLog.setExceptionDesc(e.getCause().getMessage());
			throw new HandlerException(reqId, e.getCause());
		}
		return result;
	}


}
