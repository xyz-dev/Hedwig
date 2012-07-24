/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.util.HedwigAbortPolicy;
import com.yihaodian.architecture.hedwig.common.util.HedwigThreadFactory;

/**
 * @author Archer
 *
 */
public class HedwigExecutors {

	public static ThreadPoolExecutor newCachedThreadPool(BlockingQueue<Runnable> eventQueue) {
		int coreSize = ProperitesContainer.getInstance().getIntProperty(PropKeyConstants.HEDWIG_SYNC_POOL_CORESIZE,InternalConstants.DEFAULT_SYNC_POOL_CORESIZE);
		int maxSize = ProperitesContainer.getInstance().getIntProperty(PropKeyConstants.HEDWIG_SYNC_POOL_MAXSIZE,InternalConstants.DEFAULT_SYNC_POOL_MAXSIZE);
		long idleTime = ProperitesContainer.getInstance().getLongProperty(PropKeyConstants.HEDWIG_SYNC_POOL_IDLETIME,InternalConstants.DEFAULT_SYNC_POOL_IDLETIME);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(coreSize, maxSize, idleTime, TimeUnit.SECONDS, eventQueue, new HedwigThreadFactory(),
				new HedwigAbortPolicy());
		return tpe;
	}

}
