package com.huya.search.restful.http;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Names;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;


/** 
* @ClassName: HttpResponse 
* @Description: TODO(与具体http协议实现的server无关的http响应) 
* @author LinYonfa
* @date Apr 26, 2016 3:02:35 PM 
*  
*/
public class HttpResponse extends HttpMessage {

	/**
	 * bid server使用的两个http status
	 */
	public static final int OK = HttpResponseStatus.OK.code();
	public static final int NO_CONTENT = HttpResponseStatus.NO_CONTENT.code();
	public static final int FOUND = HttpResponseStatus.FOUND.code();
	public static final int NOT_FOUND = HttpResponseStatus.NOT_FOUND.code();
	public static final int MOVED_PERMANENTLY = HttpResponseStatus.MOVED_PERMANENTLY.code();
	public static final int FORBIDDEN = HttpResponseStatus.FORBIDDEN.code();
	public static final int METHOD_NOT_ALLOWED = HttpResponseStatus.METHOD_NOT_ALLOWED.code();
	public static final int INTERNAL_SERVER_ERROR = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();

	/**
	 * 曝光和转化的contenttype类型
	 */
	public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
	public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=UTF-8";
	public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";



	private String location;
	private String contentType;

	public HttpResponse() {
	}

	/**
	 * 
	 * @param nettyReq
	 */
	public HttpResponse(FullHttpRequest nettyReq) {

		this.version = nettyReq.getProtocolVersion();
		// 默认开启长连接
		if (nettyReq.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)) {
			// 若显示申明close，则关闭
			this.keepAlive = false;
		} else if (nettyReq.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
				&& !nettyReq.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true)) {
			// http1.0，非keeyalive则默认关闭
			this.keepAlive = false;
		}
	}

	/**
	 * 转化为原生http请求，及netty请求
	 * 
	 * @return
	 */
	public FullHttpResponse nativeHttpResponse() {

		FullHttpResponse fullHttpResponse;

		ByteBuf buf = ((null == content) ? copiedBuffer(new byte[0]) : copiedBuffer(content));
		fullHttpResponse = new DefaultFullHttpResponse(this.version, HttpResponseStatus.valueOf(status), buf);

		// 这里根据为曝光或者竞价响应类型确定
		String contentType = (Strings.isNullOrEmpty(this.contentType) ? CONTENT_TYPE_PLAIN : this.contentType);
		fullHttpResponse.headers().set(CONTENT_TYPE, contentType);
		fullHttpResponse.headers().set(CONTENT_LENGTH, buf.readableBytes());
		fullHttpResponse.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");

		if (this.keepAlive) {
			fullHttpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		} else {
			fullHttpResponse.headers().set(CONNECTION, HttpHeaders.Values.CLOSE);
		}
		
		if(FOUND == status && !Strings.isNullOrEmpty(location)){
			fullHttpResponse.headers().set(Names.LOCATION, location);
		}

		// write cookie to response
		if (!cookies.isEmpty()) {
			// Reset the cookies if necessary.
			fullHttpResponse.headers()
					.add("P3P", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
			for (Cookie cookie : cookies) {
				if (null != cookie)
					fullHttpResponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
			}
		}

		return fullHttpResponse;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	public void setContent(String content) {
		// TODO Auto-generated method stub
		
	}
}
