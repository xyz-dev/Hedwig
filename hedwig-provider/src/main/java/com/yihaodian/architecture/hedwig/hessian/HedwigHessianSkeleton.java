/**
 * 
 */
package com.yihaodian.architecture.hedwig.hessian;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;
import com.yihaodian.architecture.hedwig.common.util.HedwigContextUtil;

/**
 * @author Archer
 *
 */
public class HedwigHessianSkeleton extends AbstractSkeleton {
	private static final Logger log = Logger.getLogger(HedwigHessianSkeleton.class.getName());

	private Object _service;

	/**
	 * Create a new hessian skeleton.
	 * 
	 * @param service
	 *            the underlying service object.
	 * @param apiClass
	 *            the API interface
	 */
	public HedwigHessianSkeleton(Object service, Class apiClass) {
		super(apiClass);

		if (service == null)
			service = this;

		_service = service;

		if (!apiClass.isAssignableFrom(service.getClass()))
			throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
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

			context.addHeader(header, value);
			HedwigContextUtil.setAttribute(header, value);
		}

		String methodName = in.readMethod();
		Method method = getMethod(methodName);

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
		Object[] values = new Object[args.length];

		for (int i = 0; i < args.length; i++) {
			values[i] = in.readObject(args[i]);
		}

		Object result = null;

		try {
			HedwigContextUtil.setArguments(values);
			result = method.invoke(_service, values);
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();

			log.log(Level.WARNING, e.toString(), e);

			out.startReply();
			out.writeFault("ServiceException", e.getMessage(), e);
			out.completeReply();
			return;
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
