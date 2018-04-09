package com.huya.search.restful.interceptor;

import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.http.HttpRequest;
import com.huya.search.restful.http.HttpResponse;
import com.huya.search.restful.rest.RestActionRouter;
import com.huya.search.restful.rest.RestRequest;
import com.huya.search.restful.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 管理转发dispatcher
 * 
 * @author kewn
 *
 */
public class BDispatcherInterceptor implements ApiInterceptor {

	private final static Logger LOG = LoggerFactory.getLogger(BDispatcherInterceptor.class);

	@Override
	public void execute(InterceptorChainContext<RestRequest, RestResponse> chain) {

		/**
		 * 针对管理接口做安全校验，ip+token
		 */
		HttpRequest httpReq = chain.request().httpRequest();
		HttpResponse httpResp = chain.response().httpResponse();

		// rest的http接口， 规范
		// http://domain.com/admin/version/path/action(get,post,update)/filter(?limit)
		// ?limit=10：指定返回记录的数量
		// ?offset=10：指定返回记录的开始位置。
		// ?page=2&per_page=100：指定第几页，以及每页的记录数。
		// ?sortby=name&order=asc：指定返回结果按照哪个属性排序，以及排序顺序。
		// ?animal_type_id=1：指定筛选条件

		RestActionRouter.Path pathAction = RestActionRouter.getPathAction(httpReq);

		if (null == pathAction) {
			httpResp.setStatus(HttpResponse.NOT_FOUND);
			httpResp.setContent("request resource is not found".getBytes());
			return;
		}
		
		try {
			//todo 开发阶段暂时屏蔽权限验证
			// RestAction权限校验
//			pathAction.getAction().auth(chain);

			Method m = pathAction.getMethod();
			m.invoke(pathAction.getAction(), chain);

		} catch (IllegalAccessException | IllegalArgumentException e) {
			httpResp.setStatus(HttpResponse.INTERNAL_SERVER_ERROR);
			httpResp.setContent(("action invoke exception:\r\n" + e.getClass().getSimpleName()).getBytes());
			LOG.error("action invoke exception", e);
		} catch (InvocationTargetException e) {
			httpResp.setStatus(HttpResponse.INTERNAL_SERVER_ERROR);
			httpResp.setContent(("action invoke exception:\r\n" + e.getTargetException().toString()).getBytes());
			LOG.error("action invoke exception", e);
		}
//		catch (ActionException e) {
//			httpResp.setStatus(HttpResponse.FORBIDDEN);
//			httpResp.setContent(e.getMessage().getBytes());
//			LOG.error("exe action err", e);
//		}
	}
}
