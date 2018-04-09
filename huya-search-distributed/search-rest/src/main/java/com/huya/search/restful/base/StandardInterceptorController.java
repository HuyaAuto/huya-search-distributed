package com.huya.search.restful.base;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StandardInterceptorController<Req extends UserRequest, Resp extends UserResponse> implements InterceptorController<Req, Resp>, EventListenable<Req, Resp>{

	protected List<? extends Interceptor<Req, Resp>> interceptors;

	@SuppressWarnings("rawtypes")
	protected Map<Event, Set<EventListener>> listenerMap = Maps.newHashMap();

	public StandardInterceptorController(List<? extends Interceptor<Req, Resp>> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public List<? extends Interceptor<Req, Resp>> getInterceptors() {
		return this.interceptors;
	}

	@Override
	public void onRequest(Req request, Resp response) {
		InterceptorChainContext<Req, Resp> chain = InterceptorChainContext.create(this, request, response);
		chain.proceed();
	}

	@Override
	public void addListener(Event event, EventListener<Req, Resp> listener) {
		if (null == listenerMap.get(event)) {
			this.listenerMap.put(event, Sets.newHashSet(new EventListener[] { listener }));
		}
		else {
			this.listenerMap.get(event).add(listener);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fireEvent(Event event, EventContext<Req, Resp> eventContext) {

		if (null == listenerMap.get(event))
			return;
		for (EventListener<Req, Resp> listener : listenerMap.get(event)) {
			listener.doAction(eventContext);
		}
	}

}
