package com.yihaodian.architecture.hedwig.balancer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

public class RoundRobinBalancer extends AbstractRRBalancer {

	private Lock lock = new ReentrantLock();

	protected ServiceProfile doSelect() {
		ServiceProfile sp = null;
		lock.lock();
		try {
			if (profileQueue != null) {
				for (int i = 0; i < profileQueue.size(); i++) {
					sp = profileQueue.poll();

					if (sp.isAvailable()) {
						profileQueue.add(sp);
						break;
					} else {
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
