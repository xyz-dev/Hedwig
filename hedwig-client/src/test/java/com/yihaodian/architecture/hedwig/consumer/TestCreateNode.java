/**
 * 
 */
package com.yihaodian.architecture.hedwig.consumer;

import junit.framework.TestCase;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * @author Archer Jiang
 *
 */
public class TestCreateNode extends TestCase {
	public static String PATH = "/TheStore/UnknowDomain/testService/0.1";
	public static String serverList = "192.168.35.17:2181";
	public static String hostIp;
	public static String CPATH = "";
	public void testCreate() {
		ZkClient _zkCLient = new ZkClient(serverList);
		ServiceProfile sp = (ServiceProfile) _zkCLient
				.readData("/TheStore/search/search/mandyService/1.2.4-dev/192.168.16.198:757@ubuntu");
		System.out.println(sp);
	}

}
