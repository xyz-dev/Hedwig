/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Archer
 *
 */
public class SyncRequestEvent extends BaseEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8464312440807572894L;

	public SyncRequestEvent(HedwigContext context, MethodInvocation invocation) {
		super();
		this.context = context;
		this.invocation = invocation;
	}

}
