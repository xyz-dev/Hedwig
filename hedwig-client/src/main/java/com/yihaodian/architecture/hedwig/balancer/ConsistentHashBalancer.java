/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.Collection;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer Jiang
 *
 */
public class ConsistentHashBalancer implements LoadBalancer<ServiceProfile> {

	@Override
	public ServiceProfile select() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProfiles(Collection<ServiceProfile> serviceSet) {
		// TODO Auto-generated method stub

	}

}
