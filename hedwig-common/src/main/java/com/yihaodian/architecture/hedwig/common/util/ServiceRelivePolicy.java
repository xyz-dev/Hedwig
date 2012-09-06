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

	int tryCount = 0;
	int threshold = InternalConstants.DEFAULT_RELIVE_THRESHOLD;
	@Override
	public boolean tryRelive() {
		boolean value = false;
		tryCount++;
		if (tryCount >= threshold) {
			value = true;
			resetCount();
		}
		return value;
	}

	private void resetCount() {
		tryCount = 0;
	}
	
}
