/**
 * 
 */
package com.yihaodian.architecture.hedwig.provider;

import java.io.IOException;
import java.util.Date;

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
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.caucho.services.server.ServiceContext;
import com.yihaodian.architecture.hedwig.common.config.ProperitesContainer;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.constants.PropKeyConstants;
import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.exception.InvalidParamException;
import com.yihaodian.architecture.hedwig.common.hessian.HedwigHessianExporter;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigMonitorUtil;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.register.IServiceProviderRegister;
import com.yihaodian.architecture.hedwig.register.RegisterFactory;
import com.yihaodian.monitor.dto.ServerBizLog;
import com.yihaodian.monitor.util.MonitorConstants;
import com.yihaodian.monitor.util.MonitorJmsSendUtil;

/**
 * @author Archer Jiang
 * 
 */
public class HedwigWebserviceExporter extends HedwigHessianExporter implements HttpRequestHandler, InitializingBean, DisposableBean,
		ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(HedwigWebserviceExporter.class);
	private IServiceProviderRegister register;
	private ServiceProfile profile;
	private AppProfile appProfile;
	private String serviceName;
	private String serviceVersion;
	private ApplicationContext springContext;
	private ServerBizLog sbLog;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Date start =new Date();
		sbLog = new ServerBizLog();
		sbLog.setGetReqTime(start);
		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" },
					"HessianServiceExporter only supports POST requests");
		}
		try {
			ServiceContext.begin(request, profile.getServiceName(), profile.getServicePath());
			sbLog.setProviderApp(profile.getServiceAppName());
			sbLog.setProviderHost(ProperitesContainer.provider().getProperty(PropKeyConstants.HOST_IP));
			sbLog.setServiceName(profile.getServiceName());
			invoke(request.getInputStream(), response.getOutputStream());
			sbLog.setRespResultTime(new Date());
			sbLog.setSuccessed(MonitorConstants.SUCCESS);
		} catch (Throwable ex) {
			sbLog.setRespResultTime(new Date());
			sbLog.setInParamObjects(HedwigContextUtil.getArguments());
			sbLog.setSuccessed(MonitorConstants.FAIL);
			sbLog.setExceptionClassname(HedwigMonitorUtil.getExceptionClassName(ex));
			sbLog.setExceptionDesc(HedwigMonitorUtil.getExceptionMsg(ex));
			throw new NestedServletException("Hessian skeleton invocation failed", ex);
		} finally {
			sbLog.setReqId(HedwigContextUtil.getRequestId());
			sbLog.setUniqReqId(HedwigContextUtil.getGlobalId());
			try {
				MonitorJmsSendUtil.asyncSendServerBizLog(sbLog);
			} catch (Exception e) {
			}
			HedwigContextUtil.clean();

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
			profile.setDomainName(appProfile.getDomainName());
			profile.setServiceAppName(appProfile.getServiceAppName());
			profile.setUrlPattern(appProfile.getUrlPattern());
			if(appProfile.getPort()>0){
				profile.setPort(appProfile.getPort());
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
		if (appProfile == null) {
			throw new InvalidParamException("appContexts must not blank!!!");
		}
		ServiceProfile p = new ServiceProfile();
		if (HedwigUtil.isBlankString(serviceName)) {
			serviceName = lookupServiceName();
		}
		p.setServiceName(serviceName);
		if (HedwigUtil.isBlankString(serviceVersion)) {
			throw new InvalidParamException("serviceVersion must not blank!!!");
		}
		p.setServiceVersion(serviceVersion);
		return p;
	}


	private String lookupServiceName() throws InvalidParamException {
		String name = null;
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(springContext, this.getClass());
		if (names != null && names.length >= 1) {
			for(String beanName:names){
				HedwigHessianExporter hhe = (HedwigHessianExporter)springContext.getBean(beanName);
				if(hhe.getServiceInterface().equals(getServiceInterface())){
					if(beanName.startsWith("/")){
						name = beanName.replaceFirst("/", "");
					}
				}
			}
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

	public void setProfile(ServiceProfile profile) {
		this.profile = profile;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;
	}
	public AppProfile getAppProfile() {
		return appProfile;
	}
	public void setAppProfile(AppProfile appProfile) {
		this.appProfile = appProfile;
	}



}
