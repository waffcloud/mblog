/*
 * Copyright 2016 qyh.me
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.qyh.blog.core.exception;

import me.qyh.blog.core.entity.Lock;
import me.qyh.blog.core.entity.LockResource;
import me.qyh.blog.core.message.Message;

/**
 * 锁异常
 * 
 * @author Administrator
 *
 */
public class LockException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Lock lock;
	private final LockResource lockResource;
	private final Message error;

	/**
	 * @param lock
	 *            锁
	 * @param lockResource
	 *            锁保护的资源
	 * @param error
	 *            错误信息
	 */
	public LockException(Lock lock, LockResource lockResource, Message error) {
		super(null, null, false, false);
		this.lock = lock;
		this.lockResource = lockResource;
		this.error = error;
	}

	public Lock getLock() {
		return lock;
	}

	public LockResource getLockResource() {
		return lockResource;
	}

	public Message getError() {
		return error;
	}
}
