/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;
import java.util.Set;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.RequestType;

/**
 * @author Archer Jiang
 *
 */
public class ClientProfile extends BaseProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5339046889514181081L;
	private String balanceAlgo = InternalConstants.BALANCER_NAME_WEIGHTED_ROUNDROBIN;
	private String target = "";
	private long timeout = ProperitesContainer.client().getLongProperty(PropKeyConstants.HEDWIG_READ_TIMEOUT,
			InternalConstants.DEFAULT_READ_TIMEOUT);
	private boolean profileSensitive = false;
	private String requestType = RequestType.SyncPool.getName();
	private String clientAppName;
	private boolean redoAble = false;
	private Set<String> noRetryMethods;
	private Set<String> groupNames;

	public Set<String> getGroupNames() {
		return groupNames;
	}

	public boolean isRedoAble() {
		return redoAble;
	}

	public void setRedoAble(boolean redoAble) {
		this.redoAble = redoAble;
	}

	public void setGroupNames(Set<String> groupName) {
		this.groupNames = groupName;
	}

	public ClientProfile() {
		super();
		ProperitesContainer.client();
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		if (target != null) {
			this.target = target;
			String[] arr = target.split("/", 6);
			if (arr != null && arr.length > 0) {
				this.serviceName = arr[arr.length - 1];
			}
		}
	}

	public String getBalanceAlgo() {
		return balanceAlgo;
	}

	public void setBalanceAlgo(String balanceAlgo) {
		this.balanceAlgo = balanceAlgo;
	}

	public void setBalanceAlg(String balanceAlgo) {
		this.balanceAlgo = balanceAlgo;
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

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getClientAppName() {
		return clientAppName;
	}

	public void setClientAppName(String clientAppName) {
		this.clientAppName = clientAppName;
	}

	public Set<String> getNoRetryMethods() {
		return noRetryMethods;
	}

	public void setNoRetryMethods(Set<String> noRetryMethods) {
		this.noRetryMethods = noRetryMethods;
	}

	@Override
	public String toString() {
		return "\nClientProfile [balanceAlg=" + balanceAlgo + ", target=" + target + ", timeout=" + timeout
				+ ", profileSensitive=" + profileSensitive + "," + "groupNames=" + groupNames + ","
				+ super.toString()
				+ "]";
	}

}
