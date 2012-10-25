/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer
 *
 */
public abstract class AbstractBalancer implements LoadBalancer<ServiceProfile> {

	protected Circle<Integer, ServiceProfile> profileCircle = new Circle<Integer, ServiceProfile>();
	protected Lock lock = new ReentrantLock();
	protected AtomicInteger position = new AtomicInteger();

	@Override
	public ServiceProfile select() {
		if (profileCircle == null || profileCircle.size() == 0) {
			return null;
		} else if (profileCircle.size() == 1) {
			ServiceProfile sp = profileCircle.firstVlue();
			return sp.isAvailable() ? sp : null;
		} else {
			return doSelect();
		}
	}

	protected abstract ServiceProfile doSelect();

	protected ServiceProfile getProfileFromCircle(int code) {
		int size = profileCircle.size();
		ServiceProfile sp = null;
		if (size > 0) {
			int tmp = code;
			while (size > 0) {
				tmp = profileCircle.lowerKey(tmp);
				sp = profileCircle.get(tmp);
				if (sp != null && sp.isAvailable()) {
					break;
				}
				size--;
			}
		}
		return sp;
	}
}
