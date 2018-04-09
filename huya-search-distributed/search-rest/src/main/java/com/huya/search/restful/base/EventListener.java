package com.huya.search.restful.base;

/**
 *  监听器
 * @author kewn
 *
 */
public interface EventListener<Req extends UserRequest, Resp extends UserResponse> {
    
    /**
     * 回调doAction
     */
    public void doAction(EventContext<Req, Resp> event);
    
}
