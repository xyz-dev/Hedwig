/**
 * 
 */
package com.yihaodian.architecture.hedwig.register;

import java.util.Date;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;
import com.yihaodian.architecture.zkclient.IZkStateListener;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * @author Archer
 * 
 */
public class ServiceProviderZkRegister implements IServiceProviderRegister {

	private Logger logger = LoggerFactory.getLogger(ServiceProviderZkRegister.class);
	private ZkClient _zkClient = null;
	private String parentPath = "";
	private String childPath = "";
	private boolean isRegisted = false;

	public ServiceProviderZkRegister() throws HedwigException {
		String serverList = ProperitesContainer.provider().getProperty(PropKeyConstants.ZK_SERVER_LIST);
		_zkClient = ZkUtil.getZkClientInstance();
	}

	@Override
	public void regist(final ServiceProfile profile) throws InvalidParamException {
		createZnodes(profile);
		_zkClient.subscribeStateChanges(new IZkStateListener() {

			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				logger.debug(InternalConstants.LOG_PROFIX + "zk connection state change to:" + state.toString());
			}

			@Override
			public void handleNewSession() throws Exception {
				logger.debug(InternalConstants.LOG_PROFIX + "Reconnect to zk!!!");
				if (!_zkClient.exists(childPath)) {
					_zkClient.createEphemeral(childPath, profile);
				}
			}
		});
		isRegisted = true;

	}

	private void createZnodes(ServiceProfile profile) throws InvalidParamException {
		String rollPath = "";
		String refugeePath = "";
		// create base path
		parentPath = profile.getParentPath();
		if (!_zkClient.exists(parentPath)) {
			_zkClient.createPersistent(parentPath, true);
		}
		// create ephemeral node
		childPath = ZkUtil.createChildPath(profile);
		if (!_zkClient.exists(childPath)) {
			profile.setRegistTime(new Date());
			_zkClient.createEphemeral(childPath, profile);
		}
		// create roll path
		rollPath = ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_ROLL);
		if (!_zkClient.exists(rollPath)) {
			_zkClient.createPersistent(rollPath);
		}
		// create refugee path
		refugeePath = ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_CAMPS + "/"
				+ InternalConstants.HEDWIG_PAHT_REFUGEE);
		if (!_zkClient.exists(refugeePath)) {
			_zkClient.createEphemeral(refugeePath);
		}
		// create ip node
		String ipNode = rollPath + "/" + ProperitesContainer.provider().getProperty(PropKeyConstants.HOST_IP);
		if (!_zkClient.exists(ipNode)) {
			_zkClient.createEphemeral(ipNode);
		}
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
