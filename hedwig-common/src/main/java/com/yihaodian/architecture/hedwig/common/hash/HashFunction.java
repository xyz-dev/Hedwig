/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.hash;

/**
 * @author Archer Jiang
 *
 */
public interface HashFunction {

	public int hash(Object data, int seed);

	public int hash(Object data);
}
