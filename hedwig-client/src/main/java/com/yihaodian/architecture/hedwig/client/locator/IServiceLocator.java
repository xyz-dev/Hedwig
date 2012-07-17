/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.locator;

import java.util.Collection;

/**
 * Use to get all the service provider node from register center. This class should be node quantity sensitive.When add new node or some
 * node died it should be update service node list then notify load balance component
 * 
 * @author Archer Jiang
 * 
 */
public interface IServiceLocator<E> {

	public E getService();

	public Collection<E> getAllService();

}
