package com.huya.search.restful.http;

import com.huya.search.restful.http.error.MethodNotAllowedReceiver;
import com.huya.search.restful.http.error.RouteNotFoundReceiver;

import java.util.Set;
import java.util.TreeSet;


public class HttpRouter {
	
	private static final HttpRouter INSTANCE = new HttpRouter();
	
	public static void regRoute(HttpRoute route) {
		HttpRouter.INSTANCE.routes.add(route);
	}

	private static final HttpReceiver notFoundReceiver = new RouteNotFoundReceiver();

	private static final HttpReceiver methodNotAllowedReceiver = new MethodNotAllowedReceiver();

	private final Set<HttpRoute> routes;
	
	
	private HttpRouter() {
		this.routes = new TreeSet<>((route1, route2) -> Integer.compare(route1.getPath().compareTo(route2.getPath()), 0));
	}

	@SuppressWarnings("unchecked")
	public static void receive(HttpReceiverContext context) {
		String path = context.httpRequest().getPath();
		HttpRoute route = null;

		for (HttpRoute httpRoute : HttpRouter.INSTANCE.routes) {
			if (path.startsWith(httpRoute.getPath())) {
				route = httpRoute;
				break;
			}
		}

		if (route != null) {
			String method = context.httpRequest().getMethod();
			if (!route.getMethods().contains(method)) {
				methodNotAllowedReceiver.receive(context);
			}
			else {
				route.getReceiver().receive(context);
			}
		}
		else {
			notFoundReceiver.receive(context);
		}
	}

}