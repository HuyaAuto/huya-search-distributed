package com.huya.search.restful.base;

/**
 * 请求拦截器
 * 
 * @author kewn
 *
 * @param <Req>
 * @param <Resp>
 */
public interface Interceptor<Req extends UserRequest, Resp extends UserResponse> {
    
    // Executes the interceptor's action.
    void execute(InterceptorChainContext<Req, Resp> chain);
    
}