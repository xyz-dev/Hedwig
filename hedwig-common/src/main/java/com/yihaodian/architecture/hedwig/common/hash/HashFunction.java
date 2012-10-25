/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.hash;

/**
 * @author Archer Jiang
 *
 */
public interface HashFunction {

	public int hash32(Object data);

	public long hash64(Object data);
}
