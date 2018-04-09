package com.huya.search.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.huya.search.Releasable;
import com.huya.search.SearchException;
import com.huya.search.settings.Settings;

/**
 * @author colin.ke keqinwu@yy.com
 */
public abstract class AbstractLifecycleService extends AbstractService implements LifecycleService, Releasable {

	protected final Lifecycle lifecycle = new Lifecycle();

	private final CountDownLatch startLatch = new CountDownLatch(1);

	private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();

	protected AbstractLifecycleService(Settings settings) {
		super(settings);
	}

	protected AbstractLifecycleService() {
		super();
	}

	@Override
	public Lifecycle.State lifecycleState() {
		return this.lifecycle.state();
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public void start() throws SearchException {
		if (!lifecycle.canMoveToStarted()) {
			return;
		}
		listeners.forEach(LifecycleListener::beforeStart);
		doStart();
		lifecycle.moveToStarted();
		listeners.forEach(LifecycleListener::afterStart);
		startLatch.countDown();
	}

	protected abstract void doStart() throws SearchException;

	@SuppressWarnings({"unchecked"})
	@Override
	public void stop() throws SearchException {
		if (!lifecycle.canMoveToStopped()) {
			return;
		}
		listeners.forEach(LifecycleListener::beforeStop);
		lifecycle.moveToStopped();
		doStop();
		listeners.forEach(LifecycleListener::afterStop);
	}

	protected abstract void doStop() throws SearchException;

	@Override
	public void close() throws SearchException {
		if (lifecycle.started()) {
			stop();
		}
		if (!lifecycle.canMoveToClosed()) {
			return;
		}
		listeners.forEach(LifecycleListener::beforeClose);
		lifecycle.moveToClosed();
		doClose();
		listeners.forEach(LifecycleListener::afterClose);
	}

	protected abstract void doClose() throws SearchException;

	public void waitForStart() throws InterruptedException {
		startLatch.await();
	}

	public void waitForStart(long timeVal, TimeUnit timeUnit) throws InterruptedException {
		startLatch.await(timeVal, timeUnit);
	}

	public boolean started() {
		return lifecycle.started();
	}

}
