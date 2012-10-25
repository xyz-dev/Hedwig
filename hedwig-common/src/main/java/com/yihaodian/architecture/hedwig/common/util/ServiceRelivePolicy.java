/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

/**
 * @author Archer
 * 
 */
public class ServiceRelivePolicy implements RelivePolicy {

	private int tryCount = 0;
	private int threshold = InternalConstants.DEFAULT_RELIVE_THRESHOLD;

	@Override
	public boolean tryRelive() {
		boolean value = false;
		tryCount++;
		if (tryCount >= threshold) {
			tryCount = 0;
			value = true;
		}
		return value;
	}

}
