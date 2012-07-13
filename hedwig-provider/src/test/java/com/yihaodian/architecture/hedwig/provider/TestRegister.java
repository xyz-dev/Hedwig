/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import junit.framework.TestCase;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.register.ServiceProviderZkRegister;

/**
 * @author Archer Jiang
 *
 */
public class TestRegister extends TestCase {

	public static void testRegist() throws HedwigException {
		ServiceProviderZkRegister register = new ServiceProviderZkRegister();
		ServiceProfile sp = new ServiceProfile();
		try {
			register.regist(sp);
			Thread.currentThread().sleep(500000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
