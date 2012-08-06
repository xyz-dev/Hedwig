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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.DispatcherServlet;
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
public class HedwigWebserviceExporter extends HessianExporter implements HttpRequestHandler, InitializingBean, DisposableBean,
		ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(HedwigWebserviceExporter.class);
	private IServiceProviderRegister register;
	private ServiceProfile profile;
	private String appName;
	private String serviceName;
	private String serviceVersion;
	private String urlPattern;
	private ApplicationContext springContext;

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
		p.setServiceAppName(lookupAppName());
		p.setServiceName(lookupServiceName());
		if (HedwigUtil.isBlankString(serviceVersion)) {
			throw new InvalidParamException("serviceVersion must not blank!!!");
		}
		p.setServiceVersion(serviceVersion);
		if (HedwigUtil.isBlankString(urlPattern)) {
			urlPattern = InternalConstants.HEDWIG_URL_PATTERN;
		}
		p.setUrlPattern(urlPattern);
		return p;
	}


	private String lookupAppName() throws InvalidParamException {
		String app = null;
		Object obj = BeanFactoryUtils.beanOfType(springContext, DispatcherServlet.class);
		if (obj != null) {
			DispatcherServlet ds = (DispatcherServlet) obj;
			app = ds.getServletContext().getServletContextName();
		}
		if (app == null) {
			throw new InvalidParamException("appNamem must not blank!!!");
		}
		return app;
	}

	private String lookupServiceName() throws InvalidParamException {
		String name = null;
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(springContext, getServiceInterface());
		if (names != null && names.length >= 1) {
			name = names[0];
		}
		if (HedwigUtil.isBlankString(name)) {
			throw new InvalidParamException("serviceName must not blank!!!");
		}
		return name;
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;

	}

}
