/**
 * 
 */
package com.yihaodian.architecture.hedwig.client.hessian;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianRemoteObject;

/**
 * @author Archer
 * 
 */
public class HedwigHessianProxyFactory extends HessianProxyFactory {

	public URLConnection openConnection(URL url) throws IOException {
		URLConnection conn = super.openConnection(url);
		return conn;
	}

	public Object create(Class api, String urlName, ClassLoader loader) throws MalformedURLException {
		if (api == null)
			throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
		InvocationHandler handler = null;

		URL url = new URL(urlName);
		handler = new HedwigHessianProxy(this, url);

		return Proxy.newProxyInstance(loader, new Class[] { api, HessianRemoteObject.class }, handler);
	}

	public AbstractHessianOutput getHessianOutput(OutputStream os) {
		AbstractHessianOutput out = new HedwigHessianOutput(os);

		out.setSerializerFactory(getSerializerFactory());

		return out;
	}

}
