package com.huya.search.restful.base;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户请求
 * 
 * @author kewn
 *
 */
public abstract class UserRequest {

	protected Map<String, Object> attrs = Maps.newHashMap();

	protected UserRequest() {
	}
	
	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Object> attrs) {
		this.attrs = attrs;
	}
	
	public Object getAtt(String key){
		if(null == this.attrs)
			return null;
		return this.attrs.get(key);
	}
	
	public void putAttr(String key, Object obj){
		if(null == attrs){
			attrs = new HashMap<String, Object>();
		}
		attrs.put(key, obj);
	}

}
