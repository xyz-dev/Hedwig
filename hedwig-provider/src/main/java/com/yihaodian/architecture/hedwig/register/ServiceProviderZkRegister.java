/**
 * 
 */
package com.yihaodian.architecture.hedwig.register;

import java.util.Date;
import java.util.List;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;
import com.yihaodian.architecture.zkclient.IZkChildListener;
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
		_zkClient = ZkUtil.getZkClientInstance();
	}

	@Override
	public void regist(final ServiceProfile profile) throws InvalidParamException {
		createPersistentZnodes(profile);
		createEphemeralZnodes(profile);
		_zkClient.subscribeStateChanges(new IZkStateListener() {

			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				logger.debug(InternalConstants.LOG_PROFIX + "zk connection state change to:" + state.toString());
			}

			@Override
			public void handleNewSession() throws Exception {
				logger.debug(InternalConstants.LOG_PROFIX + "Reconnect to zk!!!");
				createEphemeralZnodes(profile);
			}
		});
		_zkClient.subscribeChildChanges(parentPath, new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				createEphemeralZnodes(profile);
			}
		});
		_zkClient.subscribeChildChanges(ZkUtil.createBaseCampPath(profile), new IZkChildListener() {

			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				ZkUtil.createRefugeePath(profile);
			}
		});
		isRegisted = true;

	}

	private void createEphemeralZnodes(ServiceProfile profile) throws InvalidParamException {
		// create service node
		childPath = ZkUtil.createChildPath(profile);
		if (!_zkClient.exists(childPath)) {
			profile.setRegistTime(new Date());
			_zkClient.createEphemeral(childPath, profile);
		}
		// create ip node
		String ipNode = ZkUtil.createRollPath(profile) + "/" + ZkUtil.getProcessDesc(profile);
		if (!_zkClient.exists(ipNode)) {
			_zkClient.createEphemeral(ipNode);
		}
	}

	private void createPersistentZnodes(ServiceProfile profile) throws InvalidParamException {
		String rollPath = "";
		String refugeePath = "";
		// create base path
		parentPath = profile.getParentPath();
		if (!_zkClient.exists(parentPath)) {
			_zkClient.createPersistent(parentPath, true);
		}
		// create roll path
		rollPath = ZkUtil.createRollPath(profile);
		// create refugee path
		refugeePath = ZkUtil.createRefugeePath(profile);
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
		_zkClient.unsubscribeAll();
		if (_zkClient.exists(servicePath)) {
			_zkClient.delete(servicePath);
		}
		isRegisted = false;
	}

}
