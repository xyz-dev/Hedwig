package com.yihaodian.architecture.hedwig.common.util;

import java.net.MalformedURLException;
import javax.management.remote.JMXServiceURL;

public class JMXServiceURLBuilder {
	
	private static final String DEFAULT_URL_PATH_NAME = "/jmxrmi";

	private String host;

	private int port;
	
	private String protocol = "rmi";
	
	private String urlPathProtocol = "/jndi/rmi://";
	
	private String urlPathName = DEFAULT_URL_PATH_NAME;
	
	public JMXServiceURLBuilder(int port) {
		this(port, DEFAULT_URL_PATH_NAME);
	}

	public JMXServiceURLBuilder(int port, String pathName) {
		this.port = port;
		this.urlPathName = pathName.startsWith("/") ? pathName : "/" + pathName;
	}
	
	public JMXServiceURLBuilder(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public JMXServiceURL getJMXServiceURL() {
		try {
			checkHost();
			return new JMXServiceURL(protocol, host, port, getUrlPath());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Build JMXServiceURL faild, cause: ", e);
		}
	}

	private void checkHost() {
		if (this.host == null) {
			String firstNoLoopbackIP = SystemUtil.getLocalhostIp();
			this.host = firstNoLoopbackIP != null ? firstNoLoopbackIP : "0.0.0.0";
		}
	}

	public String getUrlPath(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.urlPathProtocol);
		stringBuilder.append(this.host);
		stringBuilder.append(":");
		stringBuilder.append(this.port);
		stringBuilder.append(this.urlPathName);
		return stringBuilder.toString();
	}
	
    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUrlPathProtocol() {
		return urlPathProtocol;
	}

	public void setUrlPathProtocol(String urlPathProtocol) {
		this.urlPathProtocol = urlPathProtocol;
	}

	public String getUrlPathName() {
		return urlPathName;
	}

	public void setUrlPathName(String urlPathName) {
		this.urlPathName = urlPathName;
	}
}
