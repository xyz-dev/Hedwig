package com.yihaodian.architecture.hedwig.common.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jmx.export.MBeanExportException;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.ObjectNameManager;

public class JmxUtil {
	private static Logger logger = LoggerFactory.getLogger(JmxUtil.class);
	private static MBeanExporter mbeanExporter;
	private static ConnectorServerFactoryBean serverConnector;
	private static final AtomicBoolean started = new AtomicBoolean(false);
	public static final String JMX_DOMAIN = "com.yihaodian.hedwig";
	private final static  String pathName = "jmxrmi";
	private final static  int port=3997;
	
	public static void startServer() 
	{
		 if (started.compareAndSet(false, true)) 
		 {
			 JMXServiceURLBuilder jmxServiceURLBuilder = new JMXServiceURLBuilder(port, pathName);
			 ApplicationContext ctx = new ClassPathXmlApplicationContext(   
		                new String[] { "applicationContext-jmx.xml"});  
			/* RmiRegistryFactoryBean registry=(RmiRegistryFactoryBean)ctx.getBean("registry");
			 registry.setPort(port);
			 ConnectorServerFactoryBean serverConnector=(ConnectorServerFactoryBean)ctx.getBean("serverConnector");
			 serverConnector.setServiceUrl(jmxServiceURLBuilder.getJMXServiceURL().toString());
			 */
			 mbeanExporter =(MBeanExporter)ctx.getBean("mbeanExporter");
			 serverConnector=new ConnectorServerFactoryBean();
			 try {
				serverConnector.setObjectName("connector:name=rmi");
				serverConnector.setServiceUrl(jmxServiceURLBuilder.getJMXServiceURL().toString());
				serverConnector.afterPropertiesSet();
			} catch (MalformedObjectNameException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} catch (JMException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
			 
		}
	}
	/**
	 * 动态注册
	 * @param name --example name -> project:name=BEAN CLASS
	 * @param bean
	 */
	public static void registerMBean(String name, Object bean)
	{
		if (!started.get()) {
			startServer();
		}
		try {
			mbeanExporter.registerManagedResource(bean, ObjectNameManager.getInstance(createObjectName(name)));
		} catch (MBeanExportException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
	}
	private static String createObjectName(String objectName)
	{
		String tmpObjName = objectName;
		if (!tmpObjName.contains("=")) {
			tmpObjName = "name=" + tmpObjName;
		}
		if (!tmpObjName.contains(":")) {
			tmpObjName = ":" + tmpObjName;
		}
		if (tmpObjName.startsWith(":")) {
			tmpObjName = JMX_DOMAIN+ tmpObjName;
		}
		return tmpObjName;
	}
	
	
}
