/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.BaseHandler;

/**
 * @author Archer
 *
 */
public class SyncRequestHandler extends BaseHandler<HedwigContext, Object> {

	@Override
	public Object doHandle(HedwigContext context, IEvent<Object> event) throws HandlerException {
		long start = HedwigClientUtil.getCurrentTime();
		Object result = null;
		ServiceProfile sp = context.getLocator().getService();
		Object hessianProxy = null;
		if (sp == null)
			throw new HandlerException("Can't find service provider for :" + context.getClientProfile().toString());
		String sUrl = sp.getServiceUrl();
		try {
			hessianProxy = HedwigClientUtil.getHessianProxy(context, sUrl);
		} catch (Exception e) {
			throw new HandlerException(e.getCause());
		}

		if (hessianProxy == null) {
			sp.setAvailable(false);
			context.getHessianProxyMap().remove(sUrl);
			throw new HandlerException("HedwigHessianInterceptor is not properly initialized");
		}
		try {
			MethodInvocation invocation = event.getInvocation();
			Object[] params = invocation.getArguments();
			result = invocation.getMethod().invoke(hessianProxy, params);
		} catch (Throwable e) {
			throw new HandlerException(e.getCause());
		} finally {
			System.out.println("Remote call cost:" + (HedwigClientUtil.getCurrentTime() - start));
		}
		return result;
	}

}
