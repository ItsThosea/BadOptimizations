package me.thosea.badoptimizations;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class DummyLock implements ReadWriteLock {
	public static final DummyLock INSTANCE = new DummyLock();
	private DummyLock() {}

	private final Condition dummyCondition = new Condition() {
		@Override
		public void await() {}
		@Override
		public void awaitUninterruptibly() {}
		@Override
		public long awaitNanos(long nanosTimeout) {return 0;}
		@Override
		public boolean await(long time, TimeUnit unit) {return false;}
		@Override
		public boolean awaitUntil(Date deadline) {return false;}
		@Override
		public void signal() {}
		@Override
		public void signalAll() {}
	};
	private final Lock dummyLock = new Lock() {
		@Override
		public void lock() {}
		@Override
		public void lockInterruptibly() {}
		@Override
		public boolean tryLock() {return true;}
		@Override
		public boolean tryLock(long time, @NotNull TimeUnit unit) {return true;}
		@Override
		public void unlock() {}
		@NotNull
		@Override
		public Condition newCondition() {
			return dummyCondition;
		}
	};

	@NotNull
	@Override
	public Lock readLock() {
		return dummyLock;
	}
	@NotNull
	@Override
	public Lock writeLock() {
		return dummyLock;
	}
}
