package com.yihaodian.architecture.hedwig.register;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;

/**
 * @author Archer
 * 
 */
public interface IServiceProviderRegister {

	public void regist(ServiceProfile profile) throws InvalidParamException;

	public void updateProfile(ServiceProfile newProfile);

	public void unRegist(ServiceProfile profile);
}
