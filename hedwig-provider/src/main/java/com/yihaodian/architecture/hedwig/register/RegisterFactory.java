/**
 * 
 */
package com.yihaodian.architecture.hedwig.register;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidReturnValueException;

/**
 * @author Archer Jiang
 *
 */
public class RegisterFactory {

	public static Map<String, IServiceProviderRegister> registers = new HashMap<String, IServiceProviderRegister>();
	private static Logger logger = LoggerFactory.getLogger(RegisterFactory.class);
	
	static {
		try {
			setRegister(InternalConstants.SERVICE_REGISTER_ZK, new ServiceProviderZkRegister());
		} catch (HedwigException e) {
			logger.error(e.getMessage());
		}
	}

	public static IServiceProviderRegister getZKRegister() throws HedwigException {
		return getRegister(ServiceProviderZkRegister.class.getName());
	}

	public static IServiceProviderRegister getRegister(String name) throws HedwigException {
		if (name == null)
			throw new InvalidParamException("Name must not null");
		IServiceProviderRegister register = null;
		if (registers.containsKey(name)) {
			register = registers.get(name);
		}
		if (register == null)
			throw new InvalidReturnValueException("Register '" + name + "' must not null");
		return register;
	}

	public static synchronized void setRegister(String name, IServiceProviderRegister register) {
		registers.put(name, register);
	}
}
