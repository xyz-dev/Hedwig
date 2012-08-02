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
import com.yihaodian.architecture.hedwig.register.IServiceProviderRegister;
import com.yihaodian.architecture.hedwig.register.RegisterFactory;

/**
 * @author Archer Jiang
 * 
 */
public class HedwigWebserviceExporter extends HessianExporter implements HttpRequestHandler, InitializingBean, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(HedwigWebserviceExporter.class);
	private IServiceProviderRegister register;
	private ServiceProfile profile = new ServiceProfile();

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
		String strService = profile.toString();
		try {
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

	public void setProfile(ServiceProfile profile) {
		this.profile = profile;
	}

}
