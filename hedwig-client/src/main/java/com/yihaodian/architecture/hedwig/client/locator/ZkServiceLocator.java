/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.locator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.balancer.BalancerFactory;
import com.yihaodian.architecture.hedwig.balancer.LoadBalancer;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.common.util.ServiceRelivePolicy;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;
import com.yihaodian.architecture.zkclient.IZkChildListener;
import com.yihaodian.architecture.zkclient.IZkDataListener;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * This locator is implemented base on zookeeper ephemeral node. It will
 * synchronize with zk server to make sure the profileContainer is up-to-date.
 * 
 * @author Archer Jiang
 * 
 */
public class ZkServiceLocator implements IServiceLocator<ServiceProfile> {

	private static Logger logger = LoggerFactory.getLogger(ZkServiceLocator.class);
	private ZkClient _zkClient = null;
	private Map<String, ServiceProfile> profileContainer = new ConcurrentHashMap<String, ServiceProfile>();
	private static boolean isProfileSensitive = false;
	private LoadBalancer<ServiceProfile> balancer;
	private boolean initialized = false;

	public ZkServiceLocator(ClientProfile clientProfile) throws HedwigException {
		super();
		isProfileSensitive = clientProfile.isProfileSensitive();
		_zkClient = ZkUtil.getZkClientInstance();
		balancer = BalancerFactory.getInstance().getBalancer(clientProfile.getBalanceAlgo());
		loadServiceProfile(clientProfile);
		balancer.updateProfiles(profileContainer.values());

	}

	private void loadServiceProfile(ClientProfile profile) {
		String parentPath = profile.getParentPath();
		List<String> childList = null;
		if (parentPath != null) {
			if (!_zkClient.exists(parentPath)) {
				logger.error("Can't find path " + parentPath + " in ZK. Can't find service provider for now");
				_zkClient.createPersistent(parentPath, true);
			}
			observeChild(parentPath);
			childList = _zkClient.getChildren(parentPath);
			observeChildData(parentPath, childList);
		}
		initialized = true;
	}

	private void observeChildData(final String parentPath, List<String> childList) {
		if (childList != null && childList.size() > 0) {
			for (String child : childList) {
				String childPath = HedwigUtil.getChildFullPath(parentPath, child);
				if (_zkClient.exists(childPath)) {
					Object obj = _zkClient.readData(childPath, true);
					addServiceProfile(child, obj);
					observeSpecifyChildData(childPath);
				}
			}
		}
	}

	private void addServiceProfile(String child, Object obj) {
		if (obj != null) {
			ServiceProfile sp = (ServiceProfile) obj;
			sp.setRelivePolicy(new ServiceRelivePolicy());
			profileContainer.put(child, sp);
		}

	}

	/**
	 * Observe specify node profile change.
	 * 
	 */
	private void observeSpecifyChildData(String childPath) {

		if (isProfileSensitive) {
			_zkClient.subscribeDataChanges(childPath, new IZkDataListener() {

				@Override
				public void handleDataDeleted(String dataPath) throws Exception {
					if (!HedwigUtil.isBlankString(dataPath)) {
						String child = HedwigUtil.getChildShortPath(dataPath);
						if (profileContainer.containsKey(child)) {
							profileContainer.remove(child);
						}
					}
					balancer.updateProfiles(profileContainer.values());
				}

				@Override
				public void handleDataChange(String dataPath, Object data) throws Exception {
					if (!HedwigUtil.isBlankString(dataPath)) {
						String child = HedwigUtil.getChildShortPath(dataPath);
						addServiceProfile(child, data);
						balancer.updateProfiles(profileContainer.values());
					}
				}
			});
		}
	}

	/**
	 * Observe the node change, add or delete
	 * 
	 * @param basePath
	 */
	private void observeChild(String basePath) {
		if (_zkClient.exists(basePath)) {
			_zkClient.subscribeChildChanges(basePath, new IZkChildListener() {

				@Override
				public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
					if (parentPath != null) {
						Map<String, ServiceProfile> newProfileMap = new HashMap<String, ServiceProfile>();
						ServiceProfile profile = null;
						String childPath;
						for (String child : currentChilds) {
							childPath = HedwigUtil.getChildFullPath(parentPath, child);
							if (!profileContainer.containsKey(child)) {
								profile = _zkClient.readData(childPath, true);
								observeSpecifyChildData(childPath);
							} else {
								profile = profileContainer.get(child);
							}
							if (profile != null) {
								newProfileMap.put(childPath, profile);
							}
						}
						profileContainer = newProfileMap;
						balancer.updateProfiles(profileContainer.values());
					}
				}
			});
		}

	}

	@Override
	public ServiceProfile getService() {
		ServiceProfile sp = null;
		while (!initialized) {
			Thread.yield();
		}
		sp = balancer.select();
		return sp;
	}

	@Override
	public Collection<ServiceProfile> getAllService() {
		return profileContainer.values();
	}

}
