/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
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

	private int threshold = 0;
	private LinkedList<Integer> hisIvkCtList = new LinkedList<Integer>();
	private AtomicInteger curIvkCount = new AtomicInteger(0);
	private int efIvkCount = 0;
	private Timer timer = new Timer();
	private Lock lock = new ReentrantLock();
	public TpsThresholdChecker(int tpsThreshold) {
		if(tpsThreshold>0){
			threshold = tpsThreshold * InternalConstants.DEFAULT_COLLECT_INTERVAL
					* InternalConstants.DEFAULT_MAX_COLLECT_ROUND;
			initIvkCountJob();
		}
	}

	public boolean check() {
		boolean value = false;
		if (threshold > 0) {
			value = (curIvkCount.get() + efIvkCount) >= threshold;
			if(!value){
				curIvkCount.incrementAndGet();
			}
		}
		return value;
	}

	private void initIvkCountJob() {
		final int interval = InternalConstants.DEFAULT_COLLECT_INTERVAL*InternalConstants.DEFAULT_COLLECT_INTERVAL_UNIT_SECOND;
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
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
				
			}
		}, interval, interval);


	}
}
