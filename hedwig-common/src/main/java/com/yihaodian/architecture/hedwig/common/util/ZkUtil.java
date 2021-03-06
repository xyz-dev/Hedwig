/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
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
		lock.lock();
		try {
			if (_zkClient == null) {
				String serverList = ProperitesContainer.provider().getProperty(PropKeyConstants.ZK_SERVER_LIST);
				if (!HedwigUtil.isBlankString(serverList)) {
					_zkClient = new ZkClient(serverList);
				} else {
					throw new HedwigException("ZK client initial error, serverList:" + serverList);
				}
			}
		} finally {
			lock.unlock();
		}
		return _zkClient;
	}

	public static String createChildPath(ServiceProfile profile) throws InvalidParamException {
		if (profile == null)
			throw new InvalidParamException(" Service profile must not null!!!");
		String pid = ProperitesContainer.client().getProperty(PropKeyConstants.JVM_PID);
		StringBuilder path = new StringBuilder(profile.getParentPath()).append("/").append(profile.getHostIp()).append(":").append(pid);
		return path.toString();
	}

	public static String createParentPath(BaseProfile profile) throws InvalidParamException {
		if (profile == null)
			throw new InvalidParamException(" Service profile must not null!!!");
		StringBuilder path = new StringBuilder(profile.getRootPath());
		path.append("/").append(profile.getDomainName()).append("/").append(profile.getServiceAppName()).append("/")
				.append(profile.getServiceName()).append("/")
				.append(profile.getServiceVersion());
		return path.toString();
	}
}
