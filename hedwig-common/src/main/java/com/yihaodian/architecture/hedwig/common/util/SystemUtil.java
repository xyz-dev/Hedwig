/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Archer Jiang
 * 
 */
public class SystemUtil {

	private static Logger logger = LoggerFactory.getLogger(SystemUtil.class);

	public static String getLocalhostIp() {
		String hostIp = "";
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			logger.debug("Get host IP failed!!!");
		}
		return hostIp;
	}

	public static String getJvmPid() {
		String jvmPid="";
		try {
			jvmPid = ManagementFactory.getRuntimeMXBean().getName();
		} catch (Exception e) {
			logger.debug("Get jvm pid failed!!!");
		}
		return jvmPid;
	}

}
