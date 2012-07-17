/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;

import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;

/**
 * @author Archer Jiang
 *
 */
public class ClientProfile extends BaseProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5339046889514181081L;
	private String balanceAlg = InternalConstants.BALANCER_NAME_ROUNDROBIN;
	private String target = "";
	private long timeout = 2000l;
	private boolean profileSensitive = false;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getBalanceAlg() {
		return balanceAlg;
	}

	public void setBalanceAlg(String balanceAlg) {
		this.balanceAlg = balanceAlg;
	}

	public boolean isProfileSensitive() {
		return profileSensitive;
	}

	public void setProfileSensitive(boolean profileSensitive) {
		this.profileSensitive = profileSensitive;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "ClientProfile [balanceAlg=" + balanceAlg + ", target=" + target + ", timeout=" + timeout + ", profileSensitive="
				+ profileSensitive + "," + super.toString() + "]";
	}

}
