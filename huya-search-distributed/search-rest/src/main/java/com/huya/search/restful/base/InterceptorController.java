package com.huya.search.restful.base;

import java.util.List;

/**
 * 拦截控制器,实例化interceptor chain
 * 
 * @author kewn
 *
 * @param <Req>
 * @param <Resp>
 */
public interface InterceptorController<Req extends UserRequest, Resp extends UserResponse> extends EventListenable<Req, Resp>{
    
    /**
     * interceptor chain
     * @return
     */
    List<? extends Interceptor<Req, Resp>> getInterceptors();

    // Runs the business logic for an interceptor request.
    void onRequest(Req request, Resp response);
    
}
