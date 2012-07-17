/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.event;

import com.yihaodian.architecture.hedwig.client.event.handle.SyncRequestHandler;

/**
 * @author Archer
 *
 */
public class EventBuilder {

	public BaseEvent createSyncReqEvent(HedwigContext context) {
		BaseEvent event = new BaseEvent(context);
		event.setHandler(new SyncRequestHandler());
		return event;
	}
}
