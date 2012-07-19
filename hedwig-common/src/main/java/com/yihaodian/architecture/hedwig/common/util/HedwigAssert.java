/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import com.yihaodian.architecture.hedwig.common.exception.HedwigException;

/**
 * @author Archer
 *
 */
public class HedwigAssert {
	public static void isNull(Object object, String message) throws HedwigException {
		if (object == null) {
			throw new HedwigException(message);
		}
	}
}
