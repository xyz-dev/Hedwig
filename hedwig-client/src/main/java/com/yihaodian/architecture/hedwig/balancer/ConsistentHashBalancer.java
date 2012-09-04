/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer Jiang
 *
 */
public class ConsistentHashBalancer implements LoadBalancer<ServiceProfile> {

	TreeMap<Long, ServiceProfile> profileCircle = new TreeMap<Long, ServiceProfile>();
	Lock lock = new ReentrantLock();

	@Override
	public ServiceProfile select() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProfiles(Collection<ServiceProfile> serviceSet) {
		int totalWeight = getTotalWeight(serviceSet);
		int size = serviceSet.size();
		for (ServiceProfile sp : serviceSet) {
			int mirror = getMirrorFactor(size, sp.getWeighted(), totalWeight, 30);
			for (int i = 0; i < mirror; i++) {

			}
		}
	}

	private int getTotalWeight(Collection<ServiceProfile> serviceSet) {
		lock.lock();
		int value = 1;
		try {
			if (serviceSet != null && serviceSet.size() > 0) {
				for (ServiceProfile sp : serviceSet) {
					value += sp.getWeighted();
				}
			}

		} finally {
			lock.unlock();
		}
		return value;
	}

	private int getMirrorFactor(int size, int weighted, int totalWeight, int seed) {
		int value = totalWeight;
		value = seed * size * weighted / totalWeight;
		return value;
	}

	// public Long getKey()

	public static void main(String[] args) {
		ConsistentHashBalancer chb = new ConsistentHashBalancer();
		System.out.println(chb.getMirrorFactor(5, 8, 10, 30));
	}
}
