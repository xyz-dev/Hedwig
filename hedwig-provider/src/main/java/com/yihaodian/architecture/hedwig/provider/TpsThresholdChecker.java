/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigExecutors;

/**
 * @author Archer
 * 
 */
public class TpsThresholdChecker {

	private int threshold = 1000;
	private LinkedList<Integer> hisIvkCtList = new LinkedList<Integer>();
	private AtomicInteger curIvkCount = new AtomicInteger(0);
	private int efIvkCount = 0;
	private ScheduledThreadPoolExecutor executor = HedwigExecutors
			.newSchedulerThreadPool(InternalConstants.HEDWIG_PROVIDER);
	private Lock lock = new ReentrantLock();
	public TpsThresholdChecker(int tpsThreshold) {
		threshold = threshold * InternalConstants.DEFAULT_COLLECT_INTERVAL
				* InternalConstants.DEFAULT_MAX_COLLECT_ROUND;
		initIvkCountJob();
	}

	public boolean check() {
		boolean value = true;
		if (threshold > 0) {
			value = (curIvkCount.incrementAndGet() + efIvkCount) <= threshold;
		}
		return value;
	}

	private void initIvkCountJob() {
		executor.schedule(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				lock.lock();
				try {
					int count = curIvkCount.getAndSet(0);
					while (hisIvkCtList.size() >= InternalConstants.DEFAULT_MAX_COLLECT_ROUND) {
						int tmp = hisIvkCtList.removeFirst();
						efIvkCount -= tmp;
					}
					hisIvkCtList.add(count);
					efIvkCount += count;
				} finally {
					lock.unlock();
				}
				return true;
			}
		}, InternalConstants.DEFAULT_COLLECT_INTERVAL, TimeUnit.SECONDS);

	}
}
