package com.huya.search.restful.rest;

import com.google.common.collect.Maps;
import com.huya.search.restful.http.HttpRequest;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * rest接口路由配置
 * 
 * @author kewn
 *
 */
public class RestActionRouter {
	
	private static final RestActionRouter INSTANCE = new RestActionRouter();
	
	public static void regAction(RestAction action) {
		RestActionRouter.INSTANCE.addAction(action);
	}

	private final Map<String, Path> actionMap = Maps.newHashMap();

	private void addAction(RestAction action) {
		Method[] methods = action.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Rest r = m.getAnnotation(Rest.class);
			if(null == r)
				continue;
			
			actionMap.put(action.getPath() + "/" + m.getName(), new Path(m, action));
		}
	}

	public static Path getPathAction(HttpRequest req) {
		String path = req.getPath();
		for (String key : RestActionRouter.INSTANCE.actionMap.keySet()) {
			if (path.equals(key)) {
				return RestActionRouter.INSTANCE.actionMap.get(key);
			}
		}
		return null;
	}

	/**
	 * nest static path class
	 * 
	 * @author kewn
	 *
	 */
	public static class Path {

		Method method;
		RestAction action;

		public Path(Method method, RestAction action) {
			this.method = method;
			this.action = action;
		}

		public Method getMethod() {
			return method;
		}

		public RestAction getAction() {
			return action;
		}

	}

}
