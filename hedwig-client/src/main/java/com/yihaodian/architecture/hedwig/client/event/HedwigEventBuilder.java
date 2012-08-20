/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.client.util.HedwigClientUtil;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
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

	public HedwigEventBuilder(HedwigContext context, ClientProfile clientProfile) {
		super();
		this.context = context;
		this.clientProfile = clientProfile;
	}


	public BaseEvent buildRequestEvent(MethodInvocation invocation) {
		BaseEvent event = null;
		String reqId = HedwigClientUtil.generateReqId();
		long expire = clientProfile.getTimeout();
		if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
			event = directRequestEvent(invocation);
		} else {
			event = SyncRequestEvent(invocation);
		}
		if (expire < InternalConstants.DEFAULT_REQUEST_TIMEOUT) {
			expire = expire << 2;
		}
		event.setExpireTime(expire);
		event.setReqestId(reqId);
		return event;
	}

	private SyncRequestEvent SyncRequestEvent(MethodInvocation invocation) {
		SyncRequestEvent event = new SyncRequestEvent(invocation);
		event.setMaxRedoCount(context.getLocator().getAllService().size());
		event.setRetryable(true);
		event.setState(EventState.init);
		return event;
	}

	private DirectRequestEvent directRequestEvent(MethodInvocation invocation) {
		DirectRequestEvent event = new DirectRequestEvent(invocation);
		event.setState(EventState.init);
		return event;
	}

}
