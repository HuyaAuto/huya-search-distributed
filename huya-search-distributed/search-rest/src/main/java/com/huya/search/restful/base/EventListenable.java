package com.huya.search.restful.base;

public interface EventListenable<Req extends UserRequest, Resp extends UserResponse> {

	void addListener(Event event, EventListener<Req, Resp> listener);

	void fireEvent(Event event, EventContext<Req, Resp> eventContext);
}
