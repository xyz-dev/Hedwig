/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.Collection;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer
 *
 */
public class RRBalancer extends AbstractBalancer {

	@Override
	public void updateProfiles(Collection<ServiceProfile> serviceSet) {
		lock.lock();
		try {
			Circle<Integer, ServiceProfile> circle = new Circle<Integer, ServiceProfile>();
			int size = 0;
			for (ServiceProfile sp : serviceSet) {
				circle.put(size++, sp);
			}
			profileCircle = circle;
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected ServiceProfile doSelect() {
		int key = position.getAndIncrement();
		int totalSize = profileCircle.size();
		int realPos = key % totalSize;
		if (key > InternalConstants.INTEGER_BARRIER) {
			position.set(0);
		}
		return getProfileFromCircle(realPos);
	}


}
