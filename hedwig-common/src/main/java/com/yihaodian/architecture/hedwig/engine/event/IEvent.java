package com.yihaodian.architecture.hedwig.engine.event;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public interface IEvent<T> extends Serializable {

	public T fire();

	public long getExpireTime();

	public TimeUnit getExpireTimeUnit();

	public boolean isRetryable();

	public T getResult();

}
