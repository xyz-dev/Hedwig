/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.constants.ServiceStatus;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.common.util.RelivePolicy;
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

	/**
	 * 服务URL
	 */
	private String serviceUrl;

	/**
	 * 协议前缀
	 */
	private String protocolPrefix = InternalConstants.PROTOCOL_PROFIX_HTTP;
	/**
	 * 转发用规则
	 */
	private String urlPattern=InternalConstants.HEDWIG_URL_PATTERN;
	/**
	 * 机器IP
	 */
	private String hostIp;
	/**
	 * 进程ID
	 */
	private String jvmPid;
	/**
	 * 服务端口
	 */
	private int port = -1;
	/**
	 * 服务元数据版本
	 */
	private int revision = 0;
	/**
	 * 默认权重
	 */
	private int weighted = 2;
	/**
	 * 负载
	 */
	private double loadRate = 0.0d;
	/**
	 * 负载阀值
	 */
	private double loadThreshold = 0.9d;
	/**
	 * 当前权重
	 */
	private AtomicInteger curWeight = new AtomicInteger(weighted);
	/**
	 * 服务状态
	 */
	private AtomicInteger status = new AtomicInteger(0);
	private AtomicBoolean available = new AtomicBoolean(true);
	/**
	 * 复活策略
	 */
	private RelivePolicy relivePolicy;;
	/**
	 * 注册时间
	 */
	private Date registTime;
	/**
	 * 是否拼装appname到serviceurl中
	 */
	private boolean assembleAppName = false;

	/**
	 * 选中次数
	 */
	private AtomicLong selectedCount = new AtomicLong(0);

	public ServiceProfile() {
		super();
		ProperitesContainer container = ProperitesContainer.provider();
		hostIp = container.getProperty(PropKeyConstants.HOST_IP);
		jvmPid = container.getProperty(PropKeyConstants.JVM_PID);
		rootPath = container.getProperty(PropKeyConstants.ZK_ROOT_PATH, rootPath);
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = ProperitesContainer.provider().getIntProperty(PropKeyConstants.HOST_PORT, port);
	}

	public int getCurWeighted() {
		return curWeight.getAndDecrement();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			String pPath = ZkUtil.createParentPath(this);
			sb.append("parentPath:").append(pPath);
			sb.append("; serviceUrl:").append(getServiceUrl());
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
		boolean value = false;

		int s = status == null ? ServiceStatus.ENABLE.getCode() : status.get();
		if (s < ServiceStatus.ENABLE.getCode() && relivePolicy != null) {
			value = relivePolicy.tryRelive();
		}else{
			value = true;
		}
		return value;
	}



	public int getStatus() {
		return status.get();
	}

	public void setStatus(ServiceStatus status) {
		int code = status.getCode();
		this.status.set(code);
		if (code < 1) {
			this.available.set(false);
		} else {
			this.available.set(true);
		}
	}

	public void setStatus(AtomicInteger status) {
		this.status = status;
		if (status.get() < 1) {
			this.available.set(false);
		} else {
			this.available.set(true);
		}
	}

	public void setRelivePolicy(RelivePolicy relivePolicy) {
		this.relivePolicy = relivePolicy;
	}

	public AtomicBoolean getAvailable() {
		return available;
	}

	public void setAvailable(AtomicBoolean available) {
		this.available = available;
	}

	public Date getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}

	public boolean isAssembleAppName() {
		return assembleAppName;
	}

	public void setAssembleAppName(boolean assembleAppName) {
		this.assembleAppName = assembleAppName;
	}

	public AtomicLong getSelectedCount() {
		return selectedCount;
	}

	public void setSelectedCount(AtomicLong selectedCount) {
		this.selectedCount = selectedCount;
	}

	public void addSelectedCount() {
		long v = this.selectedCount.incrementAndGet();
		if (v < 0) {
			this.selectedCount.set(0);
		}
	}
}
