/**
 * 
 */
package com.yihaodian.architecture.hedwig.hessian;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.NestedServletException;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;
import com.yihaodian.architecture.hedwig.provider.TpsThresholdChecker;
import com.yihaodian.monitor.dto.ServerBizLog;

/**
 * @author Archer
 * 
 */
public class HedwigHessianSkeleton extends AbstractSkeleton {
	private static final Logger log = LoggerFactory.getLogger(HedwigHessianSkeleton.class);

	private Object _service;

	private TpsThresholdChecker ttc;

	private int tpsThreshold;

	/**
	 * Create a new hessian skeleton.
	 * 
	 * @param service
	 *            the underlying service object.
	 * @param apiClass
	 *            the API interface
	 */
	public HedwigHessianSkeleton(Object service, Class apiClass, int tpsThreshold) {
		super(apiClass);
		this.tpsThreshold = tpsThreshold;
		if (service == null)
			service = this;

		_service = service;

		if (!apiClass.isAssignableFrom(service.getClass())) {
			throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
		}
		ttc = new TpsThresholdChecker(tpsThreshold);
	}

	/**
	 * Invoke the object with the request from the input stream.
	 * 
	 * @param in
	 *            the Hessian input stream
	 * @param out
	 *            the Hessian output stream
	 */
	public void invoke(AbstractHessianInput in, AbstractHessianOutput out) throws Throwable {
		ServiceContext context = ServiceContext.getContext();

		// backward compatibility for some frameworks that don't read
		// the call type first
		in.skipOptionalCall();

		String header;
		while ((header = in.readHeader()) != null) {
			Object value = in.readObject();
			if (context != null) {
				context.addHeader(header, value);
			}
			if (value != null) {
				HedwigContextUtil.setAttribute(header, value);
			}
		}

		String methodName = in.readMethod();
		final Method method = getMethod(methodName);

		if (method != null) {
		} else if ("_hessian_getAttribute".equals(methodName)) {
			String attrName = in.readString();
			in.completeCall();

			String value = null;

			if ("java.api.class".equals(attrName))
				value = getAPIClassName();
			else if ("java.home.class".equals(attrName))
				value = getHomeClassName();
			else if ("java.object.class".equals(attrName))
				value = getObjectClassName();

			out.startReply();

			out.writeObject(value);

			out.completeReply();

			return;
		} else if (method == null) {
			out.startReply();
			out.writeFault("NoSuchMethodException", "The service has no method named: " + in.getMethod(), null);
			out.completeReply();
			return;
		}

		Class[] args = method.getParameterTypes();
		final Object[] values = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			values[i] = in.readObject(args[i]);
		}

		Object result = null;
		ServerBizLog sbLog = null;
		Object obj = HedwigContextUtil.getAttribute(InternalConstants.HEDWIG_MONITORLOG, new ServerBizLog());
		sbLog = (ServerBizLog) obj;
		try {
			HedwigContextUtil.setArguments(values);
			if (ttc.isReached()) {
				throw new HedwigException("Exceed service capacity, tpsThreshold:" + tpsThreshold);
			}
			sbLog.setMethodName(method.getName());
			result = method.invoke(_service, values);
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();
			log.debug(e.getMessage(), e);
			out.startReply();
			out.writeFault("ServiceException", e.getMessage(), e);
			out.completeReply();
			throw new NestedServletException(e.getMessage(),e);
		} finally {
			sbLog.setRespResultTime(new Date());
			Object objDate = HedwigContextUtil.getAttribute(InternalConstants.HEDWIG_INVOKE_TIME, null);
			if (objDate != null) {
				sbLog.setReqTime((Date) objDate);
			}
			// The complete call needs to be after the invoke to handle a
			// trailing InputStream
			in.completeCall();

			out.startReply();

			out.writeObject(result);

			out.completeReply();
			out.close();
		}


	}
}
