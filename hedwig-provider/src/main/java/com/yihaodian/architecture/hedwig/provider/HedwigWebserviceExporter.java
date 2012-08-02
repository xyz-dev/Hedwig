/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.caucho.services.server.ServiceContext;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.register.IServiceProviderRegister;
import com.yihaodian.architecture.hedwig.register.RegisterFactory;

/**
 * @author Archer Jiang
 * 
 */
public class HedwigWebserviceExporter extends HessianExporter implements HttpRequestHandler, InitializingBean, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(HedwigWebserviceExporter.class);
	private IServiceProviderRegister register;
	private ServiceProfile profile;
	private String appName;
	private String serviceName;
	private String serviceVersion;
	private String urlPattern;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" },
					"HessianServiceExporter only supports POST requests");
		}
		ServiceContext.begin(request, profile.getServiceName(), profile.getServiceUrl());
		try {
			invoke(request.getInputStream(), response.getOutputStream());
			Object obj = ServiceContext.getContextHeader(InternalConstants.HEDWIG_REQUEST_ID);
			String reqId = obj == null ? "" : (String) obj;
			System.out.println(reqId);
		} catch (Throwable ex) {
			throw new NestedServletException("Hessian skeleton invocation failed", ex);
		} finally {
			ServiceContext.end();
		}

	}

	@Override
	public void destroy() throws Exception {
		register.unRegist(profile);

	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		try {
			if (profile == null) {
				profile = createServiceProfile();
			}
			String strService = profile.toString();
			if (register == null) {
				register = RegisterFactory.getRegister(InternalConstants.SERVICE_REGISTER_ZK);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Starting regist service " + strService);
			}
			register.regist(profile);
			if (logger.isDebugEnabled()) {
				logger.debug("Ending regist service " + strService);
			}
		} catch (Throwable e) {
			logger.debug(e.getMessage());
			if (e instanceof HedwigException) {
				System.exit(1);
			}
		}

	}

	private ServiceProfile createServiceProfile() throws InvalidParamException {
		ServiceProfile p = new ServiceProfile();
		if (HedwigUtil.isBlankString(appName)) {
			throw new InvalidParamException("appName must not blank!!!");
		}
		p.setServiceAppName(appName);
		if (HedwigUtil.isBlankString(serviceName)) {
			throw new InvalidParamException("serviceName must not blank!!!");
		}
		p.setServiceName(serviceName);
		if (HedwigUtil.isBlankString(serviceVersion)) {
			throw new InvalidParamException("serviceVersion must not blank!!!");
		}
		p.setServiceVersion(serviceVersion);
		if (!HedwigUtil.isBlankString(urlPattern)) {
			p.setUrlPattern(urlPattern);
		}
		return p;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public void setProfile(ServiceProfile profile) {
		this.profile = profile;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
