/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.dto.BaseProfile;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * @author Archer Jiang
 * 
 */
public class ZkUtil {

	public static ZkClient _zkClient = null;
	public static Lock lock = new ReentrantLock();

	public static ZkClient getZkClientInstance() throws HedwigException {

		if (_zkClient == null) {
			lock.lock();
			if (_zkClient == null) {
				try {
					String serverList = ProperitesContainer.provider().getProperty(PropKeyConstants.ZK_SERVER_LIST);
					if (!HedwigUtil.isBlankString(serverList)) {
						_zkClient = new ZkClient(serverList);
					} else {
						throw new HedwigException("ZK client initial error, serverList:" + serverList);
					}
				} finally {
					lock.unlock();
				}
			}
		}
		return _zkClient;
	}

	public static String createChildPath(ServiceProfile profile) throws InvalidParamException {
		if (profile == null)
			throw new InvalidParamException(" Service profile must not null!!!");
		StringBuilder path = new StringBuilder(profile.getParentPath()).append("/").append(getProcessDesc(profile));
		return path.toString();
	}

	public static String getProcessDesc(ServiceProfile profile) throws InvalidParamException {
		StringBuilder path = new StringBuilder().append(profile.getHostIp()).append(":").append(profile.getPort());
		return path.toString();
	}

	public static String createParentPath(BaseProfile profile) throws InvalidParamException {
		if (profile == null)
			throw new InvalidParamException(" Service profile must not null!!!");
		StringBuilder path = new StringBuilder(profile.getRootPath());
		path.append("/").append(profile.getDomainName()).append("/").append(profile.getServiceAppName()).append("/")
				.append(profile.getServiceName()).append("/").append(profile.getServiceVersion());
		return path.toString();
	}

	public static String generatePath(BaseProfile profile, String subPath) throws InvalidParamException {
		String value = "";
		if (profile == null && subPath != null)
			throw new InvalidParamException(" Service profile must not null!!!");
		StringBuilder path = new StringBuilder(profile.getRootPath());
		path.append("/").append(profile.getDomainName()).append("/").append(profile.getServiceAppName()).append("/")
				.append(subPath);
		value = path.toString();
		if (!_zkClient.exists(value)) {
			_zkClient.createPersistent(value, true);
		}
		return value;
	}

	public static String createRollPath(BaseProfile profile) throws InvalidParamException {
		return ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_ROLL);
	}

	public static String createRefugeePath(BaseProfile profile) throws InvalidParamException {
		return ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_CAMPS + "/"
				+ InternalConstants.HEDWIG_PAHT_REFUGEE);
	}

	public static String createCampPath(BaseProfile profile, String campName) throws InvalidParamException {
		return ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_CAMPS + "/" + campName);
	}

	public static String createBaseCampPath(BaseProfile profile) {
		String value = "";
		try {
			value = ZkUtil.generatePath(profile, InternalConstants.HEDWIG_PAHT_CAMPS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
