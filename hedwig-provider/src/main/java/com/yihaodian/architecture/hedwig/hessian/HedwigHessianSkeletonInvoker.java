package com.yihaodian.architecture.hedwig.hessian;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.Assert;

import com.caucho.hessian.io.SerializerFactory;

abstract class HedwigHessianSkeletonInvoker {

	/**
	 * Wrapped HessianSkeleton, available to subclasses.
	 */
	protected final HedwigHessianSkeleton skeleton;

	/**
	 * Hessian SerializerFactory (if any), available to subclasses.
	 */
	protected final SerializerFactory serializerFactory;


	/**
	 * Create a new HessianSkeletonInvoker for the given skeleton.
	 * @param skeleton the HessianSkeleton to wrap
	 * @param serializerFactory the Hessian SerializerFactory to use, if any
	 */
	public HedwigHessianSkeletonInvoker(HedwigHessianSkeleton skeleton, SerializerFactory serializerFactory) {
		Assert.notNull(skeleton, "HessianSkeleton must not be null");
		this.skeleton = skeleton;
		this.serializerFactory = serializerFactory;
	}


	/**
	 * Invoke the given skeleton based on the given input/output streams.
	 * @param inputStream the stream containing the Hessian input
	 * @param outputStream the stream to receive the Hessian output
	 * @throws Throwable if the skeleton invocation failed
	 */
	public abstract void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable;
}
