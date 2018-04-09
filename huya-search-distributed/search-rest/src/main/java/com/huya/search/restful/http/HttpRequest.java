package com.huya.search.restful.http;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
* @ClassName: HttpRequest 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author LinYonfa
* @date Dec 2, 2015 4:17:30 PM 
*  
*/
public class HttpRequest extends HttpMessage {

	private String remoteIp;// remote ip
	private String localIp;// local ip
	private String referer;
	private String path;
	private HttpMethod method;
	private Map<String, String> paramsMap;
	private Map<String, List<String>> parameterMap;

	private ChannelHandlerContext ctx;
	private FullHttpRequest nettyRequest;

	private String clientIp;// client ip

	public HttpRequest() {
	}

	public HttpRequest(FullHttpRequest req) {
		this.nettyRequest = req;
		this.method = req.getMethod();
		QueryStringDecoder decoder = new QueryStringDecoder(nettyRequest.getUri());
		this.path = decoder.path();
		this.parameterMap = decoder.parameters();
		this.content = req.content().copy().array();
		this.referer = req.headers().get(Names.REFERER);
		postRequest(req);

		// 代理转发真实ip
		// X-Forwarded
		// REMOTE_ADDR
		// X-Real-IP
		String realIp = req.headers().get("X-Real-IP");
		String forward = req.headers().get("X-Forwarded");
		String remote = req.headers().get("REMOTE_ADDR");
		this.clientIp = (null != realIp ? realIp : (null != forward ? forward : (null != remote ? remote : null)));
	}

	private void postRequest(FullHttpRequest req) {

		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
		try{
			List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
			// 读取从客户端传过来的参数
			for (InterfaceHttpData data : postList) {
				String name = data.getName();
				String value;
				if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
					MemoryAttribute attribute = (MemoryAttribute) data;
					attribute.setCharset(CharsetUtil.UTF_8);
					value = attribute.getValue();
					List<String> list = new ArrayList<>(1);
					list.add(value);
					try {
						parameterMap.put(name, list);
					} catch (RuntimeException ignored) {}
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public HttpRequest(FullHttpRequest req, ChannelHandlerContext ctx) {
		this(req);

		SocketAddress addRemote = ctx.channel().remoteAddress();
		this.remoteIp = (null != addRemote ? ((InetSocketAddress) addRemote).getAddress().getHostAddress() : null);

		SocketAddress addLocal = ctx.channel().remoteAddress();
		this.localIp = (null != addLocal ? ((InetSocketAddress) addLocal).getAddress().getHostAddress() : null);

		this.ctx = ctx;
	}

	public String getMethod() {
		return null == method ? null : method.name();
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Map<String, String> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取请求路径
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public String getLocalIp() {
		return localIp;
	}

	public String getClientIp() {
		if (Strings.isNullOrEmpty(clientIp)) {
			return remoteIp;
		}
		return clientIp;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public ChannelHandlerContext nettyChannelHandlerContext() {
		return this.ctx;
	}

	public String getReferer() {
		return referer;
	}

	/**
	 * 获取请求路径url参数
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getParameter(String key) {
		if (null != parameterMap) {
			return parameterMap.get(key);
		}
		return null;
	}

	/**
	 * 获取url参数中第一个
	 * 
	 * @param key
	 * @return null when no value or list.get(0)
	 */
	public String getParameterFirst(String key) {
		List<String> vList = parameterMap.get(key);
		if (null == vList || vList.isEmpty()) {
			return null;
		}
		return vList.get(0);
	}

	public FullHttpRequest nettyHttpRequest() {
		return this.nettyRequest;
	}

	/**
	 * 添加参数
	 * 
	 * @param key
	 *            url参数key
	 * @param value
	 *            url key对应value值
	 */
	public void addParameter(String key, String value) {

		if (this.parameterMap == null)
			this.parameterMap = Maps.newHashMap();

		if (this.parameterMap.get(key) == null) {
			this.parameterMap.put(key, Lists.newArrayList(value));
		} else {
			this.parameterMap.get(key).add(value);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("clientip:").append(clientIp).append(" remoteip:").append(remoteIp).append(" -").append(method).append(" -").append(nettyRequest.getUri())
				.append(" refer:").append(referer);
		return sb.toString();
	}

}
