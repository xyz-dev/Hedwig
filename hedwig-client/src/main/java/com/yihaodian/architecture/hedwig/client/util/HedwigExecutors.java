/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yihaodian.architecture.hedwig.common.util.HedwigAbortPolicy;
import com.yihaodian.architecture.hedwig.common.util.HedwigThreadFactory;

/**
 * @author Archer
 *
 */
public class HedwigExecutors {

	public static ExecutorService newCachedThreadPool() {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 30, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue(),new HedwigThreadFactory(),new HedwigAbortPolicy());
		return tpe;
	}

	public static ExecutorService newCachedThreadPool(BlockingQueue<Runnable> eventQueue) {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 30, 5L, TimeUnit.MINUTES, eventQueue, new HedwigThreadFactory(),
				new HedwigAbortPolicy());
		return tpe;
	}

}
