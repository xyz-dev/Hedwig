/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer
 *
 */
public abstract class AbstractRRBalancer implements LoadBalancer<ServiceProfile>{

	protected Queue<ServiceProfile> profileQueue = new ConcurrentLinkedQueue<ServiceProfile>();

	
	@Override
	public ServiceProfile select() {
		if (profileQueue == null || profileQueue.size() == 0) {
			return null;
		} else if (profileQueue.size() == 1) {
			return profileQueue.peek();
		} else {
			return doSelect();
		}
	}
	
	protected abstract ServiceProfile doSelect();

	@Override
	public void updateProfiles(Collection<ServiceProfile> serviceSet) {
		profileQueue = new ConcurrentLinkedQueue<ServiceProfile>(serviceSet);

	}
	
}
