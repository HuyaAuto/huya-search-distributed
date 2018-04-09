package com.huya.search.restful.interceptor.provider;

import com.google.common.collect.Lists;
import com.huya.search.restful.interceptor.ASecurityInterceptor;
import com.huya.search.restful.interceptor.ApiInterceptor;
import com.huya.search.restful.interceptor.BDispatcherInterceptor;

import java.util.List;

/**
 *
 * @author kewn
 * @date 创建时间：2015年8月10日 上午10:13:58
 */
public class ApiInterceptorProvider {
	
	public static List<ApiInterceptor> get() {

		List<ApiInterceptor> list = Lists.newArrayList();

		ApiInterceptor a = new ASecurityInterceptor();
		ApiInterceptor b = new BDispatcherInterceptor();

		list.add(a);
		list.add(b);

		return list;
	}
}
