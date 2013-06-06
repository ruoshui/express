package cn.wang.yin.utils;


import cn.wang.yin.hessian.api.Remot;

import com.caucho.hessian.client.HessianProxyFactory;

public class RemoteFactoryUtils {
	private static HessianProxyFactory factory = null;
	private static Remot remot;

	public static HessianProxyFactory getFactory() {
		if (factory == null) {
			factory = new HessianProxyFactory();
			factory.setOverloadEnabled(true);
			factory.setHessian2Reply(false);
			factory.setChunkedPost(false);

		}
		return factory;
	}

}
