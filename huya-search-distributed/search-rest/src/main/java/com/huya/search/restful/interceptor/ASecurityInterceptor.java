package com.huya.search.restful.interceptor;

import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.http.HttpRequest;
import com.huya.search.restful.http.HttpResponse;
import com.huya.search.restful.rest.RestRequest;
import com.huya.search.restful.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASecurityInterceptor implements ApiInterceptor {
	
	private final Logger LOG = LoggerFactory.getLogger(ASecurityInterceptor.class);

	@Override
	public void execute(InterceptorChainContext<RestRequest, RestResponse> chain) {
		
		HttpRequest httpReq = chain.request().httpRequest();
		HttpResponse httpResp = chain.response().httpResponse();

		//todo 以后添加相应的安全设置，测试开发阶段暂时屏蔽
		//接口安全拦截
//		ApiConfig config = ApiConfig.instance();
//		List<String> ipWhite = config.getIpWhiteList();
//
//		if(!IpUtils.rCheckIpZone(ipWhite, httpReq.getClientIp())){
//			httpResp.setStatus(HttpResponse.FORBIDDEN);
//			httpResp.setContent("invalid ip".getBytes());
//			LOG.error(String.format("admin request not in white ip list, client ip:%s", httpReq.getClientIp()));
//			return ;
//		}
		
		chain.proceed();
	}

}
