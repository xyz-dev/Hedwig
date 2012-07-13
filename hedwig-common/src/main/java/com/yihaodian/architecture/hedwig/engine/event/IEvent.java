package com.yihaodian.architecture.hedwig.engine.event;

import java.io.Serializable;

public interface IEvent extends Serializable {

	public void fire();

}
