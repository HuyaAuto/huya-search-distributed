package com.huya.search.service;

import com.huya.search.SearchException;

/**
 * @author colin.ke keqinwu@yy.com
 */
public interface LifecycleService extends Service  {

	Lifecycle.State lifecycleState();

	void addLifecycleListener(LifecycleListener listener);

	void removeLifecycleListener(LifecycleListener listener);

	void start() throws SearchException;

	void stop() throws SearchException;
}
