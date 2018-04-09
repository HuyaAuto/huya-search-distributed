package com.huya.search.restful.base;

/**
 * http receiver接口抽象类
 * 
 * @author kewn
 *
 * @param <C>
 */
public abstract class RequestReceiver<C extends InterceptorController<?, ?>, T extends RequestReceiverContext> {

	protected C controller;

	public RequestReceiver(C controller) {
		this.controller = controller;
	}

	/**
	 * @return
	 */
	protected final C controller() {
		return this.controller;
	}

	/**
	 * @param context
	 */
	public abstract void receive(T context);
	
	public abstract String getPath();

}