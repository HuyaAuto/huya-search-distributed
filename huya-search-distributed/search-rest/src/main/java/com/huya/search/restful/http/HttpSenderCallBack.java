package com.huya.search.restful.http;

/** 
* @ClassName: HttpSenderCallBack 
* @Description: TODO(http 请求响应回调) 
* @author LinYonfa
* @date Apr 26, 2016 3:01:46 PM 
*  
*/
@SuppressWarnings("rawtypes")
public interface HttpSenderCallBack {

	/**
	 * 丢弃处理
	 */
	public static HttpSenderCallBack DISCARD = new HttpSenderCallBack() {

		@Override
		public void receive(HttpSenderContext context) {
			return ;
		}

	};

	/**
	 * http请求响应回调
	 * 
	 */
	public void receive(HttpSenderContext context) throws Exception;
	
}
