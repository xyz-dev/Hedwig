package com.yihaodian.architecture.hedwig.common.constants;

public interface InternalConstants {

	public static String BASE_ROOT = "/TheStore/Hedwig";
	public static String PROPERITIES_FILE_NAME = "hedwig.properties";
	public static String PROPERITIES_PATH_KEY = "hedwig_config";

	public static String HASH_FUNCTION_MUR2 = "murmur2";

	public static String LOG_PROFIX = "Hedwig said:";
	public static String ENGINE_LOG_PROFIX = "Hedwig said:";

	public static String BALANCER_NAME_ROUNDROBIN = "RoundRobin";
	public static String BALANCER_NAME_WEIGHTED_ROUNDROBIN = "WeightedRoundRobin";
	public static String BALANCER_NAME_CONSISTENTHASH = "ConsistentHash";

	public static String SERVICE_REGISTER_ZK = "zkRegister";

	public static String PROTOCOL_PROFIX_HTTP = "http";
	public static String HEDWIG_URL_PATTERN = "hedwigService";

}
