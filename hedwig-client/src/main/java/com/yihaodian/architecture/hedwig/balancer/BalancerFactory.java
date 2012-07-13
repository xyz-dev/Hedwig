/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.HashMap;
import java.util.Map;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidReturnValueException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;

/**
 * @author Archer Jiang
 *
 */
public class BalancerFactory {

	private static BalancerFactory factory = new BalancerFactory();

	private static Map<String, LoadBalancer<ServiceProfile>> balancerContainer;

	private BalancerFactory() {
		super();
		balancerContainer =new HashMap<String, LoadBalancer<ServiceProfile>>();
		balancerContainer.put(InternalConstants.BALANCER_NAME_ROUNDROBIN, new RoundRobinBalancer());
		balancerContainer.put(InternalConstants.BALANCER_NAME_WEIGHTED_ROUNDROBIN, new WeightedRoundRobinBalancer());

	}

	public static BalancerFactory getInstance() {
		return factory;
	}

	public LoadBalancer<ServiceProfile> getBalancer(String name) throws HedwigException {
		if (HedwigUtil.isBlankString(name))
			throw new InvalidParamException("Balancer name must not null");
		LoadBalancer<ServiceProfile> b = balancerContainer.get(name);
		if (b != null) {
			return b;
		} else {
			throw new InvalidReturnValueException("Can't find " + name + " balancer");
		}
	}

}
