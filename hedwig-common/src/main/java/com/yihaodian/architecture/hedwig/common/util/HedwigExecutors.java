/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Archer
 *
 */
public class HedwigExecutors {

	public static ExecutorService newCachedThreadPool() {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 30, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue(),new HedwigThreadFactory(),new HedwigAbortPolicy());
		return tpe;
	}

}
