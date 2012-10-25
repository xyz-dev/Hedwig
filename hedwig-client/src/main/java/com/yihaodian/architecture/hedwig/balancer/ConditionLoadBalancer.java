/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

/**
 * @author Archer
 * @param <C>
 * @param <P>
 *
 */
public interface ConditionLoadBalancer<P,C> extends LoadBalancer<P> {

	public P select(C condition);
}
