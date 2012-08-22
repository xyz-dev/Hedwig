/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.hessian;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

import com.caucho.hessian.io.SerializerFactory;

/**
 * @author Archer
 *
 */
public class HedwigHessianExporter extends RemoteExporter implements InitializingBean {

	private SerializerFactory serializerFactory = new SerializerFactory();

	private Log debugLogger;

	private HedwigHessian2SkeletonInvoker skeletonInvoker;

	/**
	 * Specify the Hessian SerializerFactory to use.
	 * <p>
	 * This will typically be passed in as an inner bean definition of type
	 * <code>com.caucho.hessian.io.SerializerFactory</code>, with custom bean
	 * property values applied.
	 */
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = (serializerFactory != null ? serializerFactory : new SerializerFactory());
	}

	/**
	 * Set whether to send the Java collection type for each serialized
	 * collection. Default is "true".
	 */
	public void setSendCollectionType(boolean sendCollectionType) {
		this.serializerFactory.setSendCollectionType(sendCollectionType);
	}

	/**
	 * Set whether Hessian's debug mode should be enabled, logging to this
	 * exporter's Commons Logging log. Default is "false".
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setDebug
	 */
	public void setDebug(boolean debug) {
		this.debugLogger = (debug ? logger : null);
	}

	public void afterPropertiesSet() {
		prepare();
	}

	/**
	 * Initialize this exporter.
	 */
	public void prepare() {
		HedwigHessianSkeleton skeleton = null;

		try {
			try {
				// Try Hessian 3.x (with service interface argument).
				Constructor ctor = HedwigHessianSkeleton.class.getConstructor(new Class[] { Object.class, Class.class });
				checkService();
				checkServiceInterface();
				skeleton = (HedwigHessianSkeleton) ctor.newInstance(new Object[] { getProxyForService(), getServiceInterface() });
			} catch (NoSuchMethodException ex) {
				// Fall back to Hessian 2.x (without service interface
				// argument).
				Constructor ctor = HedwigHessianSkeleton.class.getConstructor(new Class[] { Object.class });
				skeleton = (HedwigHessianSkeleton) ctor.newInstance(new Object[] { getProxyForService() });
			}
		} catch (Throwable ex) {
			throw new BeanInitializationException("Hessian skeleton initialization failed", ex);
		}

		this.skeletonInvoker = new HedwigHessian2SkeletonInvoker(skeleton, this.serializerFactory, this.debugLogger);

	}

	/**
	 * Perform an invocation on the exported object.
	 * 
	 * @param inputStream
	 *            the request stream
	 * @param outputStream
	 *            the response stream
	 * @throws Throwable
	 *             if invocation failed
	 */
	public void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable {
		Assert.notNull(this.skeletonInvoker, "Hessian exporter has not been initialized");
		ClassLoader originalClassLoader = overrideThreadContextClassLoader();
		try {
			this.skeletonInvoker.invoke(inputStream, outputStream);
		} finally {
			resetThreadContextClassLoader(originalClassLoader);
		}
	}

}
