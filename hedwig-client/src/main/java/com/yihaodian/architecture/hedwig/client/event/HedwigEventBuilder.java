/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import org.aopalliance.intercept.MethodInvocation;

import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.engine.event.IEvent;

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


	public IEvent<HedwigContext, Object> buildRequestEvent(MethodInvocation invocation) {
		if (!HedwigUtil.isBlankString(clientProfile.getTarget())) {
			return DirectRequestEvent(invocation);
		} else {
			return SyncRequestEvent(invocation);
		}

	}

	private SyncRequestEvent SyncRequestEvent(MethodInvocation invocation) {
		SyncRequestEvent event = new SyncRequestEvent(context, invocation);
		event.setMaxRedoCount(context.getLocator().getAllService().size());
		event.setRetryable(true);
		return event;
	}

	private DirectRequestEvent DirectRequestEvent(MethodInvocation invocation) {
		return new DirectRequestEvent(context, invocation);
	}

}
