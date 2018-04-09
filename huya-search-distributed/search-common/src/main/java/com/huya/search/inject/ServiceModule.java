package com.huya.search.inject;

import java.util.List;

import com.huya.search.service.LifecycleService;

/**
 * @author colin.ke keqinwu@yy.com
 */
public interface ServiceModule {

	List<Class<? extends LifecycleService>> services();
}
