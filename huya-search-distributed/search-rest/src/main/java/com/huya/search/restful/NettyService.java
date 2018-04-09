package com.huya.search.restful;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.restful.controller.ApiController;
import com.huya.search.restful.http.HttpRoute;
import com.huya.search.restful.http.HttpRouter;
import com.huya.search.restful.http.service.HttpService;
import com.huya.search.restful.interceptor.provider.ApiInterceptorProvider;
import com.huya.search.restful.receiver.ApiReceiver;
import com.huya.search.restful.rest.RestAction;
import com.huya.search.restful.rest.RestActionRouter;
import com.huya.search.service.AbstractOrderService;
import com.huya.search.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class NettyService extends AbstractOrderService {

	private final static Logger LOG = LoggerFactory.getLogger(NettyService.class);

	private ExecutorService es = Executors.newSingleThreadExecutor();

	private HttpService http;

	private Settings settings;

	@Inject
	public NettyService(@Named("Netty") Settings settings) {
		this.settings = settings;
		http = new HttpService(this.settings);
		try {
			LOG.info("init HttpService");
			init();
		} catch (Exception e) {
			LOG.error("NettyService init fail", e);
		}
	}

	private void init() throws Exception {
		initController();
	}

	@Override
	protected void doStart() throws SearchException {
		LOG.info("Starting HttpService");
		try {
			es.submit(() -> http.start());
			http.awaitStart();
		} catch (Exception e) {
			LOG.error("Starting HttpService err", e);
		}
	}

	@Override
	protected void doStop() throws SearchException {
		http.close();
	}

	@Override
	protected void doClose() throws SearchException {
		es.shutdown();
	}

	private void initController() throws Exception {
		ApiController controller = new ApiController(ApiInterceptorProvider.get());

		ApiReceiver receiver = new ApiReceiver(controller);
		HttpRouter.regRoute(HttpRoute.create(Sets.newHashSet(HttpRoute.GET, HttpRoute.POST, HttpRoute.OPTIONS), receiver));

		List<String> list = settings.getPrefix("netty.api");

		for (String clazzName : list) {
			Class clazz = Class.forName(clazzName);
			RestAction action = (RestAction)clazz.newInstance();
			RestActionRouter.regAction(action);
			LOG.info("started action " + clazzName);
		}
	}

	@Override
	public String getName() {
		return "NettyService";
	}
}
