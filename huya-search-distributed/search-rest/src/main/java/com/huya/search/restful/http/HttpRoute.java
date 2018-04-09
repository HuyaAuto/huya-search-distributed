package com.huya.search.restful.http;

import io.netty.handler.codec.http.HttpMethod;

import java.util.HashSet;
import java.util.Set;


/** 
* @ClassName: HttpRoute 
* @Description: TODO(Http 请求路由) 
* @author LinYonfa
* @date Apr 26, 2016 3:02:15 PM 
*  
*/
public class HttpRoute {

    public static final String GET = HttpMethod.GET.name();
    public static final String POST = HttpMethod.POST.name();
    public static final String OPTIONS = HttpMethod.OPTIONS.name();


    // request path
    private String path;
    // receiver to process the request path
    private HttpReceiver<?,?> receiver;
    
    /**
     * 路由支持的请求方法
     */
    private Set<String> methods = new HashSet<String>();
    

    public HttpRoute(Set<String> methods, HttpReceiver<?,?> receiver) {
		this.path = receiver.getPath();
		this.methods = methods;
		this.receiver = receiver;
    }

    public String getPath() {
    	return path;
    }

    @SuppressWarnings("rawtypes")
    public HttpReceiver getReceiver() {
    	return receiver;
    }

    public Set<String> getMethods() {
    	return methods;
    }

    /**
     * 构件get方法路由
     * 
     * @param httpReceiver
     */
    public static HttpRoute get(HttpReceiver<?,?> httpReceiver) {
    	Set<String> methods = new HashSet<String>();
    	methods.add(GET);
    	return create(methods, httpReceiver);
    }

    /**
     * 构件post方法路由
     * 
     * @param httpReceiver
     */
    public static HttpRoute post(HttpReceiver<?,?> httpReceiver) {
    	Set<String> methods = new HashSet<String>();
    	methods.add(POST);
    	return create(methods, httpReceiver);
    }

    public static HttpRoute options(HttpReceiver<?,?> httpReceiver) {
        Set<String> methods = new HashSet<String>();
        methods.add(OPTIONS);
        return create(methods, httpReceiver);
    }

    /**
     * 创建路由
     */
    public static HttpRoute create(Set<String> methods, HttpReceiver<?,?> httpReceiver) {
    	HttpRoute route = new HttpRoute(methods, httpReceiver);
    	return route;
    }
    
    
    /**
     * 判断是否match的路由
     * @param path
     * @return
     */
    public boolean matchTo(String path) {
    	if (path.startsWith(this.path)) {
			return true;
		}
    	return false;
    }
}