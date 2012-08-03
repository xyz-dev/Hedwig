/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigCallerRunPolicy;
import com.yihaodian.architecture.hedwig.common.util.HedwigThreadFactory;

/**
 * @author Archer
 * 
 */
public class HedwigExecutors {

	public static ThreadPoolExecutor newCachedThreadPool(BlockingQueue<Runnable> eventQueue) {
		int coreSize = ProperitesContainer.client().getIntProperty(PropKeyConstants.HEDWIG_SYNC_POOL_CORESIZE,
				InternalConstants.DEFAULT_SYNC_POOL_CORESIZE);
		int maxSize = ProperitesContainer.client().getIntProperty(PropKeyConstants.HEDWIG_SYNC_POOL_MAXSIZE,
				InternalConstants.DEFAULT_SYNC_POOL_MAXSIZE);
		long idleTime = ProperitesContainer.client().getLongProperty(PropKeyConstants.HEDWIG_SYNC_POOL_IDLETIME,
				InternalConstants.DEFAULT_SYNC_POOL_IDLETIME);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(coreSize, maxSize, idleTime, TimeUnit.SECONDS, eventQueue,
				new HedwigThreadFactory(), new HedwigCallerRunPolicy());
		return tpe;
	}

	public static ScheduledThreadPoolExecutor newSchedulerThreadPool() {
		int coreSize = ProperitesContainer.client().getIntProperty(PropKeyConstants.HEDWIG_SCHEDULER_POOL_CORESIZE,
				InternalConstants.DEFAULT_SCHEDULER_POOL_CORESIZE);
		int maxSize = ProperitesContainer.client().getIntProperty(PropKeyConstants.HEDWIG_SCHEDULER_POOL_MAXSIZE,
				InternalConstants.DEFAULT_SCHEDULER_POOL_MAXSIZE);
		long idleTime = ProperitesContainer.client().getIntProperty(PropKeyConstants.HEDWIG_SCHEDULER_POOL_IDLETIME,
				InternalConstants.DEFAULT_SCHEDULER_POOL_IDLETIME);
		ScheduledThreadPoolExecutor tpe = new ScheduledThreadPoolExecutor(coreSize, new HedwigThreadFactory(), new HedwigCallerRunPolicy());
		tpe.setMaximumPoolSize(maxSize);
		tpe.setKeepAliveTime(idleTime, TimeUnit.SECONDS);

		return tpe;
	}

}
