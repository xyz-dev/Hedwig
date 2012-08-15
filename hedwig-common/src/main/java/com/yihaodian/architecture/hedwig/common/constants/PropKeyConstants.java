package com.yihaodian.architecture.hedwig.common.constants;

public interface PropKeyConstants {

	public static final String ZK_SERVER_LIST = "zk.server.list";
	public static final String ZK_ROOT_PATH = "zk.root.path";
	public static final String JVM_PID = "jvm.pid";
	public static final String HOST_IP = "host.ip";
	public static final String HOST_PORT = "host.port";
	public static final String HOST_WEIGHTED = "host.weight";

	public static final String HEDWIG_POOL_CORESIZE = "pool.size";
	public static final String HEDWIG_POOL_MAXSIZE = "pool.maxSize";
	public static final String HEDWIG_POOL_IDLETIME = "pool.idleTime";
	public static final String HEDWIG_POOL_QUEUESIZE = "pool.queueSize";

	public static final String HEDWIG_SCHEDULER_POOL_CORESIZE = "schedulerPool.size";
	public static final String HEDWIG_SCHEDULER_POOL_MAXSIZE = "schedulerPool.maxSize";
	public static final String HEDWIG_SCHEDULER_POOL_IDLETIME = "schedulerPool.idleTime";

	public static final String HEDWIG_READ_TIMEOUT = "read.timeout";
}
