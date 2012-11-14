/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.locator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ClientProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;
import com.yihaodian.architecture.zkclient.IZkChildListener;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * @author Archer
 *
 */
public class GroupTracker {

	private static Logger logger = LoggerFactory.getLogger(GroupServiceLocator.class);
	private Map<String, List<String>> campMap = new HashMap<String, List<String>>();
	private Set<String> processSet = new HashSet<String>();
	private ClientProfile clientProfile;
	private ZkClient _zkClient = null;

	public GroupTracker(ClientProfile clientProfile) throws HedwigException {
		super();
		this.clientProfile = clientProfile;
		this._zkClient = ZkUtil.getZkClientInstance();
		observeCamps();
		loadAvailableProcess();
	}

	public Collection<ServiceProfile> groupFilter(Map<String, ServiceProfile> profiles) {
		Collection<ServiceProfile> groupedProfiles = new ArrayList<ServiceProfile>();
		if (processSet == null) {
			groupedProfiles = profiles.values();
		} else {
			if (processSet.size() > 0) {
				for (String key : profiles.keySet()) {
					if (processSet.contains(key)) {
						groupedProfiles.add(profiles.get(key));
					}
				}
			}
		}
		return groupedProfiles;
	}

	/**
	 * 
	 */
	private void loadAvailableProcess() {
		Set<String> set = null;
		String baseCamp = ZkUtil.createBaseCampPath(clientProfile);
		Set<String> campSet = clientProfile.getGroupNames();
		List<String> camps = _zkClient.getChildren(baseCamp);
		if (camps != null && camps.size() > 1) {
			if (campSet == null || campSet.size() <= 0) {
				updateCampMap(InternalConstants.HEDWIG_PAHT_REFUGEE);
			} else {
				for (String campName : campSet) {
					updateCampMap(campName);
				}
			}
			processSet = totalProcess();
		} else {
			processSet = null;
		}

	}

	private Set<String> totalProcess() {
		Set<String> set = new HashSet<String>();
		for (List<String> pl : campMap.values()) {
			set.addAll(pl);
		}
		return set;
	}

	public void updateCampMap(String campName) {
		try {
			String campPath = ZkUtil.createCampPath(clientProfile, campName);
			List<String> list = _zkClient.getChildren(campPath);
			if (list != null && list.size() > 0) {
				campMap.put(campPath, list);
			}
		} catch (Exception e) {
			logger.error(InternalConstants.HANDLE_LOG_PROFIX + e.getMessage(), e);
		}

	}

	/**
	 * Observe group change, include group increase/decrease and process
	 * increase/decrease.
	 */
	private void observeCamps() {
		try {
			String baseCamp = ZkUtil.createBaseCampPath(clientProfile);
			Set<String> campSet = clientProfile.getGroupNames();
			String campPath = null;
			if (campSet != null && campSet.size() > 0) {
				for (String campName : campSet) {
					campPath = ZkUtil.createCampPath(clientProfile, campName);
					// observe available process change of interesting group
					_zkClient.subscribeChildChanges(campPath, new IZkChildListener() {

						@Override
						public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
							updateProcess(parentPath, currentChilds);

						}
					});
				}

				// observe group change add or delete
				_zkClient.subscribeChildChanges(baseCamp, new IZkChildListener() {
					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						loadAvailableProcess();

					}
				});
			} else {
				campPath = ZkUtil.createRefugeePath(clientProfile);
				// observe refugee process
				_zkClient.subscribeChildChanges(campPath, new IZkChildListener() {

					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						updateProcess(parentPath, currentChilds);

					}
				});
			}

		} catch (Exception e) {
			logger.error("Observe camps failed!!!");
		}
	}

	protected void updateProcess(String campPath, List<String> currentChilds) {
		campMap.put(campPath, currentChilds);
		processSet = totalProcess();
	}

}
