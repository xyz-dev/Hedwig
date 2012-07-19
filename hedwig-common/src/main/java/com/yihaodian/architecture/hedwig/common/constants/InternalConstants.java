package com.yihaodian.architecture.hedwig.common.constants;

public interface InternalConstants {

	public static String BASE_ROOT = "/TheStore/Hedwig";
	public static String PROPERITIES_FILE_NAME = "hedwig.properties";
	public static String PROPERITIES_PATH_KEY = "hedwig_config";

	public static String HASH_FUNCTION_MUR2 = "murmur2";

	public static String LOG_PROFIX = "Hedwig said:";
	public static String ENGINE_LOG_PROFIX = "Event engine said:";
	public static String HANDLE_LOG_PROFIX = "Event handler said:";

	public static String BALANCER_NAME_ROUNDROBIN = "RoundRobin";
	public static String BALANCER_NAME_WEIGHTED_ROUNDROBIN = "WeightedRoundRobin";
	public static String BALANCER_NAME_CONSISTENTHASH = "ConsistentHash";

	public static String SERVICE_REGISTER_ZK = "zkRegister";

	public static String PROTOCOL_PROFIX_HTTP = "http";
	public static String HEDWIG_URL_PATTERN = "hedwigService";

	public static long DEFAULT_REQUEST_TIMEOUT = 2000;
	public static long DEFAULT_READ_TIMEOUT = 1000;

}
