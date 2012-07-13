/**
 * 
 */
package com.yihaodian.architecture.hedwig.consumer;

import junit.framework.TestCase;

import org.I0Itec.zkclient.ZkClient;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;

/**
 * @author Archer Jiang
 *
 */
public class TestCreateNode extends TestCase {
	public static String PATH = "/TheStore/Hathaway/testService/0.1";
	public static String serverList = "ArcherDesktop:2181";
	public static String hostIp;
	public static String CPATH = "";
	public void testCreate() {
		ZkClient _zkCLient = new ZkClient(serverList);
		ServiceProfile bp = TestUtil.getServiceProfile();
		_zkCLient.createPersistent(bp.getParentPath(), true);
		try {
			_zkCLient.createEphemeral(ZkUtil.createChildPath(bp), bp);
			Thread.currentThread().sleep(10000000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
