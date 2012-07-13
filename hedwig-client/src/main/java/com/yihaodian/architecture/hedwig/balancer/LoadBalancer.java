package com.yihaodian.architecture.hedwig.balancer;

import java.util.Collection;

public interface LoadBalancer<P> {

	public P select();

	public void updateProfiles(Collection<P> serviceSet);

}
