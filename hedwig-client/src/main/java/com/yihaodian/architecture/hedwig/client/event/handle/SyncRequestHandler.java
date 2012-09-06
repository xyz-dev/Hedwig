/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.exception.HessianProxyException;
import com.yihaodian.architecture.hedwig.engine.exception.ProviderNotFindException;
import com.yihaodian.monitor.dto.ClientBizLog;

/**
 * @author Archer
 * 
 */
public class SyncRequestHandler extends BaseHandler {

	@Override
	public Object doHandle(HedwigContext context, IEvent<Object> event, ClientBizLog cbLog) throws HandlerException {

		Object result = null;
		ServiceProfile sp = context.getLocator().getService();
		String reqId = event.getReqestId();
		Object hessianProxy = null;
		if (sp == null)
			throw new ProviderNotFindException(reqId, " Can't find service provider for :"
					+ context.getClientProfile().toString());
		String sUrl = sp.getServiceUrl();
		String host = sp.getHostIp() + ":" + sp.getPort();
		cbLog.setProviderHost(host);
		HedwigContextUtil.setAttribute(InternalConstants.HEDWIG_SERVICE_IP, host);

		try {
			hessianProxy = HedwigClientUtil.getHessianProxy(context, sUrl);
		} catch (Exception e) {
			throw new HessianProxyException(reqId, e.getCause());
		}

		if (hessianProxy == null) {
			sp.setAvailable(false);
			throw new HessianProxyException(reqId, "Service provider is not avaliable!!! " + sp.toString());
		}
		MethodInvocation invocation = event.getInvocation();
		Object[] params = invocation.getArguments();
		try {
			result = invocation.getMethod().invoke(hessianProxy, params);
		} catch (Throwable e) {
			sp.setAvailable(checkSPAvaliable(e));
			throw new HandlerException(reqId, e.getMessage(), e.getCause());
		}

		return result;
	}

}
