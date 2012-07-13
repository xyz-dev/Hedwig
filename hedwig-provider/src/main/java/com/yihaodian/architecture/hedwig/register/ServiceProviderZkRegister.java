/**
 * 
 */
package com.yihaodian.architecture.hedwig.register;

import org.I0Itec.zkclient.ZkClient;

import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;

/**
 * @author Archer
 * 
 */
public class ServiceProviderZkRegister implements IServiceProviderRegister {

	private ZkClient _zkClient = null;
	private String parentPath = "";
	private String childPath = "";
	private boolean isRegisted = false;

	public ServiceProviderZkRegister() throws HedwigException {
		String serverList = ProperitesContainer.getInstance().getProperty(PropKeyConstants.ZK_SERVER_LIST);
		_zkClient = ZkUtil.getZkClientInstance();
	}

	@Override
	public void regist(ServiceProfile profile) throws InvalidParamException {
		parentPath = profile.getParentPath();
		if (!_zkClient.exists(parentPath)) {
			_zkClient.createPersistent(parentPath, true);
		}
		childPath = ZkUtil.createChildPath(profile);
		if (!_zkClient.exists(childPath)) {
			_zkClient.createEphemeral(childPath, profile);
		}
		isRegisted = true;

	}

	@Override
	public void updateProfile(ServiceProfile newProfile) {
		if (isRegisted) {
			ServiceProfile oldProfile = _zkClient.readData(childPath, true);
			if (oldProfile != null && newProfile != null && newProfile.getRevision() > oldProfile.getRevision()) {
				_zkClient.writeData(childPath, newProfile);
			}
		}
	}

	@Override
	public void unRegist(ServiceProfile profile) {
		String servicePath = profile.getServicePath();
		if (_zkClient.exists(servicePath)) {
			_zkClient.delete(servicePath);
		}
		isRegisted = false;
	}

}
