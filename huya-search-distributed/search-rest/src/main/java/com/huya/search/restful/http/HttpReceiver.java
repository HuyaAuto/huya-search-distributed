package com.huya.search.restful.http;


import com.huya.search.restful.base.InterceptorController;
import com.huya.search.restful.base.RequestReceiver;

/**
* @ClassName: HttpReceiver 
* @Description: TODO(Http 请求处理器) 
* @author LinYonfa
* @date Apr 26, 2016 3:03:09 PM 
*  
*/
public abstract class HttpReceiver<C extends InterceptorController<?, ?>, T extends HttpReceiverContext> extends RequestReceiver<C, T> {
    
    /**
	 * @param controller
	 */
	public HttpReceiver(C controller) {
		super(controller);
	}

	/**
     * 接收请求
     */
	@Override
	public abstract void receive(T context);
}

