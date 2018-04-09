package com.huya.search.restful.http;

import io.netty.handler.codec.http.*;

import java.util.HashSet;
import java.util.Set;

/** 
* @ClassName: HttpMessage 
* @Description: TODO(http 消息) 
* @author LinYonfa
* @date Apr 26, 2016 3:03:24 PM 
*  
*/
public class HttpMessage {

	// http verison
	protected HttpVersion version;

	// http headers
	protected HttpHeaders headers;

	// keep alive
	protected boolean keepAlive = true;

	// http status
	protected int status;

	// content response
	protected byte[] content;

	// cookies
	protected Set<Cookie> cookies = new HashSet<Cookie>();

	public HttpVersion getVersion() {
		return version;
	}

	public void setVersion(HttpVersion version) {
		this.version = version;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void addHeader(String name, String value) {
		if (null == this.headers)
			this.headers = new DefaultHttpHeaders();
		this.headers.add(name, value);
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] contents) {
		this.content = contents;
	}

	public Set<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(Set<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * cookie添加
	 * 
	 * @param name
	 * @param value
	 * @param path
	 * @param maxAge
	 */
	public void addCookie(String name, String value, String path, long maxAge) {
		Cookie cookie = new DefaultCookie(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		this.cookies.add(cookie);
	}

}
