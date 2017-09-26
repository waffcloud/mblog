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
package me.qyh.blog.core.entity;

import java.io.Serializable;

/**
 * 开锁钥匙
 * 
 * @author Administrator
 *
 */
public interface LockKey extends Serializable {

	/**
	 * 锁ID
	 * 
	 * @return
	 */
	String lockId();

	/**
	 * 得到钥匙
	 * 
	 * @return 钥匙
	 */
	Serializable getKey();

}