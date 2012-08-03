/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;

/**
 * @author Archer Jiang
 * 
 */
public class ServiceProfile extends BaseProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6012531717460254654L;
	private String servicePath;
	private String serviceUrl;
	private String protocolPrefix = InternalConstants.PROTOCOL_PROFIX_HTTP;
	private String urlPattern=InternalConstants.HEDWIG_URL_PATTERN;
	private String hostIp;
	private String jvmPid;
	private String port = "8080";
	private int revision = 0;
	private int weighted = 1;
	private double loadRate = 0.0d;
	private double loadThreshold = 0.9d;
	private AtomicInteger curWeight = new AtomicInteger(weighted);
	private AtomicBoolean available = new AtomicBoolean(true);

	public ServiceProfile() {
		super();
		ProperitesContainer container = ProperitesContainer.provider();
		hostIp = container.getProperty(PropKeyConstants.HOST_IP);
		jvmPid = container.getProperty(PropKeyConstants.JVM_PID);
		port = container.getProperty(PropKeyConstants.HOST_PORT, port);
		parentPath = container.getProperty(PropKeyConstants.ZK_ROOT_PATH, parentPath);
		weighted = HedwigUtil.ParseString2Int(container.getProperty(PropKeyConstants.HOST_WEIGHTED), 1);
	}

	public String getServiceUrl() {
		if (HedwigUtil.isBlankString(serviceUrl)) {
			serviceUrl = HedwigUtil.generateServiceUrl(this);
		}
		return serviceUrl;
	}

	public String getProtocolPrefix() {
		return protocolPrefix;
	}

	public void setProtocolPrefix(String protocolPrefix) {
		this.protocolPrefix = protocolPrefix;
	}

	public String getJvmPid() {
		return jvmPid;
	}

	public void setJvmPid(String jvmPid) {
		this.jvmPid = jvmPid;
	}

	public AtomicInteger getCurWeight() {
		return curWeight;
	}

	public void setCurWeight(AtomicInteger curWeight) {
		this.curWeight = curWeight;
	}

	public void decreaseCurWeight() {
		curWeight.decrementAndGet();
	}

	public void resetCurWeight() {
		curWeight = new AtomicInteger(weighted);
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public int getWeighted() {
		return weighted;
	}

	public void setWeighted(int weighted) {
		this.weighted = weighted;
		this.curWeight = new AtomicInteger(weighted);
	}

	public double getLoadRate() {
		return loadRate;
	}

	public void setLoadRate(double loadRate) {
		this.loadRate = loadRate;
	}

	public double getLoadThreshold() {
		return loadThreshold;
	}

	public void setLoadThreshold(double loadThreshold) {
		this.loadThreshold = loadThreshold;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getCurWeighted() {
		return curWeight.getAndDecrement();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			String pPath = ZkUtil.createParentPath(this);
			sb.append(pPath);
			sb.append(ZkUtil.createChildPath(this)).append(" port:").append(port).append(" revision:").append(revision);
		} catch (InvalidParamException e) {
		}
		return sb.toString();
	}

	public String getServicePath() {
		if (HedwigUtil.isBlankString(servicePath)) {
			try {
				servicePath = ZkUtil.createChildPath(this);
			} catch (Exception e) {

			}
		}
		return servicePath;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public boolean isAvailable() {
		return available.get();
	}

	public void setAvailable(boolean available) {
		this.available.set(available);
	}
	
	
}
