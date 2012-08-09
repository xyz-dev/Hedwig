/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event.handle;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.event.HedwigContext;
import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;
import com.yihaodian.architecture.hedwig.engine.exception.HandlerException;
import com.yihaodian.architecture.hedwig.engine.handler.BaseHandler;

/**
 * @author Archer
 *
 */
public class DirectRequestHandler extends BaseHandler<HedwigContext, Object> {

	@Override
	protected Object doHandle(HedwigContext context, IEvent<Object> event) throws HandlerException {
		Object hessianProxy = null;
		Object result = null;
		String sUrl = context.getClientProfile().getTarget();
		String reqId = event.getReqestId();
		if (HedwigUtil.isBlankString(sUrl))
			throw new HandlerException(reqId, "Target url must not null!!!");
		try {
			hessianProxy = HedwigClientUtil.getHessianProxy(context, sUrl);
		} catch (Exception e) {
			throw new HandlerException(reqId, e.getCause());
		}

		if (hessianProxy == null) {
			context.getHessianProxyMap().remove(sUrl);
			throw new HandlerException(reqId, "HedwigHessianInterceptor is not properly initialized");
		}
		try {
			MethodInvocation invocation = event.getInvocation();
			result = invocation.getMethod().invoke(hessianProxy, invocation.getArguments());
		} catch (Exception e) {
			throw new HandlerException(reqId, e.getCause());
		}
		return result;
	}

}
