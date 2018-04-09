package com.huya.search.restful.rest;

/**
* @ClassName: ActionException 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author LinYonfa
* @date Apr 26, 2016 3:07:05 PM 
*  
*/
public class ActionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ActionException(String msg){
		super(msg);
	}
	
	public ActionException(String msg, Exception e){
		super(msg, e);
	}
}
