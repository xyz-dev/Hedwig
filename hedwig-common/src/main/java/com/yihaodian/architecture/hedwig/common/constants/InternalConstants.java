package com.yihaodian.architecture.hedwig.common.constants;

public interface InternalConstants {

	public static final String CONFIG_GROUP = "yihaodian_common";
	public static final String CONFIG_FILE_CLIENT = "hedwigClient.properties";
	public static final String CONFIG_FILE_PROVIDER = "hedwigProvider.properties";
	public static final String BASE_ROOT = "/TheStore";
	public static final String UNKONW_DOMAIN = "UnknowDomain";
	public static final String PROPERITIES_FILE_NAME = "hedwig.properties";
	public static final String PROPERITIES_PATH_KEY = "hedwig_config";

	public static final String HASH_FUNCTION_MUR2 = "murmur2";

	public static final String LOG_PROFIX = "Hedwig said:";
	public static final String ENGINE_LOG_PROFIX = "Event engine said:";
	public static final String HANDLE_LOG_PROFIX = "Event handler said:";

	public static final String BALANCER_NAME_ROUNDROBIN = "RoundRobin";
	public static final String BALANCER_NAME_WEIGHTED_ROUNDROBIN = "WeightedRoundRobin";
	public static final String BALANCER_NAME_CONSISTENTHASH = "ConsistentHash";

	public static final String SERVICE_REGISTER_ZK = "zkRegister";

	public static final String PROTOCOL_PROFIX_HTTP = "http";
	public static final String HEDWIG_URL_PATTERN = "hessian";

	public static final long DEFAULT_REQUEST_TIMEOUT = 3000;
	public static final long DEFAULT_READ_TIMEOUT = 1000;
	public static final long DEFAULT_WAITING = 5000;

	public static final int DEFAULT_SYNC_POOL_CORESIZE = 20;
	public static final int DEFAULT_SYNC_POOL_MAXSIZE = 30;
	public static final int DEFAULT_SYNC_POOL_IDLETIME = 60;

	public static final int DEFAULT_SCHEDULER_POOL_CORESIZE = 5;
	public static final int DEFAULT_SCHEDULER_POOL_MAXSIZE = 10;
	public static final int DEFAULT_SCHEDULER_POOL_IDLETIME = 60;

	public static final String HEDWIG_REQUEST_ID = "reqId";

}
