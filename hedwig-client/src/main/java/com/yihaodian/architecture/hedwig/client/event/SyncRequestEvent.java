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

	public SyncRequestEvent(MethodInvocation invocation) {
		super();
		this.invocation = invocation;
	}

	@Override
	public String toString() {
		return "SyncRequestEvent [" + super.toString() + "]";
	}

}
