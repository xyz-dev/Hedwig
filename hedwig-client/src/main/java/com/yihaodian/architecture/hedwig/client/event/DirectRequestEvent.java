/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Archer
 *
 */
public class DirectRequestEvent extends BaseEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 879439981664914146L;

	public DirectRequestEvent(MethodInvocation invocation) {
		super();
		this.invocation = invocation;
	}

}
