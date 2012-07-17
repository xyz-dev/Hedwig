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

	public BaseEvent<Object> createSyncReqEvent(HedwigContext context) {
		BaseEvent<Object> event = new BaseEvent<Object>(context);
		event.setHandler(new SyncRequestHandler());
		return event;
	}
}
