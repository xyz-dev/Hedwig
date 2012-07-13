/**
 * 
 */
package com.yihaodian.architecture.hedwig.balancer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;

/**
 * @author Archer Jiang
 * 
 */
public class TestBalancer extends TestCase {

	Logger logger = LoggerFactory.getLogger(TestBalancer.class);
	BlockingQueue<Runnable> bq = new LinkedBlockingQueue<Runnable>(30);
	ExecutorService es = new ThreadPoolExecutor(10, 20, 10, TimeUnit.MINUTES,bq );
	AtomicInteger ai = new AtomicInteger(0);

	public void testRoundRobin() throws InterruptedException {
		Set<ServiceProfile> serviceSet = new HashSet<ServiceProfile>();
		ServiceProfile sp = null;
		for (int i = 0; i < 100; i++) {
			sp = new ServiceProfile();
			sp.setServiceName("service" + i);
			serviceSet.add(sp);
		}
		final RoundRobinBalancer r = new RoundRobinBalancer();
		r.updateProfiles(serviceSet);
		for (int m = 0; m < 100; m++) {
			Thread.sleep(0);
			es.execute(new Runnable() {
				@Override
				public void run() {
					int a =ai.getAndIncrement();
					long start = System.nanoTime();
					r.select();
					System.out.println(Thread.currentThread().getName()+"Compute "+a+"RoundRobinBalancer Cost:" + (System.nanoTime() - start)+" blockingQueue size:"+bq.size());

				}
			});
		}
		es.shutdown();
	}

	public void testWeightedRoundRobin() throws InterruptedException {
		Set<ServiceProfile> serviceSet = new HashSet<ServiceProfile>();
		ServiceProfile sp = null;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			sp = new ServiceProfile();
			sp.setServiceName("service" + i);
			sp.setWeighted(random.nextInt(5));
			serviceSet.add(sp);
		}
		final LoadBalancer<ServiceProfile> r = new WeightedRoundRobinBalancer();
		r.updateProfiles(serviceSet);
		for (int m = 0; m < 100; m++) {
			Thread.sleep(0);
			es.execute(new Runnable() {
				@Override
				public void run() {
					int a =ai.getAndIncrement();
					long start = System.nanoTime();
					r.select();
					System.out.println(Thread.currentThread().getName()+"Compute "+a+" WeightedRoundRobinBalancer Cost:" + (System.nanoTime() - start)+" blockingQueue size:"+bq.size());

				}
			});
		}
		es.shutdown();
	}
}
