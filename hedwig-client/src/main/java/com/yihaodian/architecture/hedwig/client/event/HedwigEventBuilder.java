/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.RequestType;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.EventState;

/**
 * @author Archer
 *
 */
public class HedwigEventBuilder {

	private HedwigContext context;
	private ClientProfile clientProfile;
	private Set<String> noRetoryMethods;

	public HedwigEventBuilder(HedwigContext context, ClientProfile clientProfile) {
		super();
		this.context = context;
		this.clientProfile = clientProfile;
		this.noRetoryMethods = clientProfile.getNoRetryMethods();
	}


	public BaseEvent buildRequestEvent(MethodInvocation invocation) {
		BaseEvent event = null;
		long expire = clientProfile.getTimeout();
		if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
			event = directRequestEvent(invocation);
			event.setRequestType(RequestType.SyncInner);
		} else {
			event = SyncRequestEvent(invocation);
			event.setRequestType(RequestType.getByName(clientProfile.getRequestType()));
		}
		if (expire < InternalConstants.DEFAULT_REQUEST_TIMEOUT) {
			expire = expire << 1;
		}
		event.setExpireTime(expire);
		String reqId = HedwigClientUtil.generateReqId(event);
		event.setReqestId(reqId);
		return event;
	}

	private SyncRequestEvent SyncRequestEvent(MethodInvocation invocation) {
		SyncRequestEvent event = new SyncRequestEvent(invocation);
		String methodName = invocation.getMethod().getName();
		if (noRetoryMethods == null || !noRetoryMethods.contains(methodName)) {
			event.setMaxRedoCount(HedwigClientUtil.getRedoCount(context));
			event.setRetryable(true);
		}
		event.setState(EventState.init);
		return event;
	}

	private DirectRequestEvent directRequestEvent(MethodInvocation invocation) {
		DirectRequestEvent event = new DirectRequestEvent(invocation);
		event.setState(EventState.init);
		return event;
	}

}
