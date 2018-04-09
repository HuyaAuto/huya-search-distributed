package com.huya.search.restful.rest;

import com.huya.search.restful.base.InterceptorChainContext;

/**
* @ClassName: RestAction 
* @Description: TODO(action 基类) 
* @author LinYonfa
* @date Apr 26, 2016 3:04:44 PM 
*  
*/
public interface RestAction {

	/**
	 * action安全校验
	 * @param chain
	 * @return
	 * @throws ActionException
	 */
	public boolean auth(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException;
	
	/**
	 * 获取请求路径
	 * 
	 * @return
	 */
	public String getPath();
}
