package com.huya.search.service;

import com.huya.search.ArdbIllegalStateException;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class Lifecycle {

	public static enum State {
		INITIALIZED, STOPPED, STARTED, CLOSED
	}

	private volatile State state = State.INITIALIZED;

	public State state() {
		return this.state;
	}

	/**
	 * Returns <tt>true</tt> if the state is initialized.
	 */
	public boolean initialized() {
		return state == State.INITIALIZED;
	}

	/**
	 * Returns <tt>true</tt> if the state is started.
	 */
	public boolean started() {
		return state == State.STARTED;
	}

	/**
	 * Returns <tt>true</tt> if the state is stopped.
	 */
	public boolean stopped() {
		return state == State.STOPPED;
	}

	/**
	 * Returns <tt>true</tt> if the state is closed.
	 */
	public boolean closed() {
		return state == State.CLOSED;
	}

	public boolean stoppedOrClosed() {
		Lifecycle.State state = this.state;
		return state == State.STOPPED || state == State.CLOSED;
	}

	public boolean canMoveToStarted() throws ArdbIllegalStateException {
		State localState = this.state;
		if (localState == State.INITIALIZED || localState == State.STOPPED) {
			return true;
		}
		if (localState == State.STARTED) {
			return false;
		}
		if (localState == State.CLOSED) {
			throw new ArdbIllegalStateException(
					"Can't move to started state when closed");
		}
		throw new ArdbIllegalStateException(
				"Can't move to started with unknown state");
	}

	public boolean moveToStarted() throws ArdbIllegalStateException {
		State localState = this.state;
		if (localState == State.INITIALIZED || localState == State.STOPPED) {
			state = State.STARTED;
			return true;
		}
		if (localState == State.STARTED) {
			return false;
		}
		if (localState == State.CLOSED) {
			throw new ArdbIllegalStateException(
					"Can't move to started state when closed");
		}
		throw new ArdbIllegalStateException(
				"Can't move to started with unknown state");
	}

	public boolean canMoveToStopped() throws ArdbIllegalStateException {
		State localState = state;
		if (localState == State.STARTED) {
			return true;
		}
		if (localState == State.INITIALIZED || localState == State.STOPPED) {
			return false;
		}
		if (localState == State.CLOSED) {
			throw new ArdbIllegalStateException(
					"Can't move to started state when closed");
		}
		throw new ArdbIllegalStateException(
				"Can't move to started with unknown state");
	}

	public boolean moveToStopped() throws ArdbIllegalStateException {
		State localState = state;
		if (localState == State.STARTED) {
			state = State.STOPPED;
			return true;
		}
		if (localState == State.INITIALIZED || localState == State.STOPPED) {
			return false;
		}
		if (localState == State.CLOSED) {
			throw new ArdbIllegalStateException(
					"Can't move to started state when closed");
		}
		throw new ArdbIllegalStateException(
				"Can't move to started with unknown state");
	}

	public boolean canMoveToClosed() throws ArdbIllegalStateException {
		State localState = state;
		if (localState == State.CLOSED) {
			return false;
		}
		if (localState == State.STARTED) {
			throw new ArdbIllegalStateException(
					"Can't move to closed before moving to stopped mode");
		}
		return true;
	}

	public boolean moveToClosed() throws ArdbIllegalStateException {
		State localState = state;
		if (localState == State.CLOSED) {
			return false;
		}
		if (localState == State.STARTED) {
			throw new ArdbIllegalStateException(
					"Can't move to closed before moving to stopped mode");
		}
		state = State.CLOSED;
		return true;
	}

	@Override
	public String toString() {
		return state.toString();
	}
}
