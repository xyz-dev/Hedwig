/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer
 *
 */
public class WeightedRoundRobinBalancer extends AbstractRRBalancer{

	protected Lock lock = new ReentrantLock();
	@Override
	protected ServiceProfile doSelect() {
		ServiceProfile sp = null;
		lock.lock();
		try {
			if (profileQueue != null) {
				for (int i = 0; i < profileQueue.size(); i++) {
					sp = profileQueue.peek();
					if (sp.isAvailable()) {
						if (sp.getCurWeighted() <= 1) {
							profileQueue.poll();
							sp.resetCurWeight();
							profileQueue.add(sp);
						} else {
							sp.decreaseCurWeight();
						}
						break;
					} else {
						profileQueue.poll();
						sp.resetCurWeight();
						profileQueue.add(sp);
						sp = null;
					}

				}

			}
		} finally {
			lock.unlock();
		}
		return sp;
	}

}
