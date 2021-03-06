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
package me.qyh.blog.comment.module;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import me.qyh.blog.comment.entity.Comment;
import me.qyh.blog.core.entity.Space;
import me.qyh.blog.core.exception.LogicException;

public abstract class CommentModuleHandler {

	// 模块类型
	private final String type;

	public CommentModuleHandler(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	/**
	 * 在插入评论之前做校验
	 * 
	 * @param id
	 *            项目ID
	 * @throws LogicException
	 */
	public abstract void doValidateBeforeInsert(Integer id) throws LogicException;

	/**
	 * 在查询评论前做校验
	 * 
	 * @param id
	 * @return 是否校驗通过
	 */
	public abstract boolean doValidateBeforeQuery(Integer id);

	/**
	 * 查询多个项目的评论数
	 * 
	 * @param ids
	 * @return
	 */
	public abstract Map<Integer, Integer> queryCommentNums(Collection<Integer> ids);

	/**
	 * 查詢某个项目的评论数
	 * 
	 * @param id
	 * @return
	 */
	public abstract OptionalInt queryCommentNum(Integer id);

	/**
	 * 查詢某個空間下所有項目的評論總數
	 * 
	 * @param space
	 *            空间
	 * @param queryPrivate
	 *            是否查询私人项目
	 * @return
	 */
	public abstract int queryCommentNum(Space space, boolean queryPrivate);

	/**
	 * 根据项目ID查询项目明细
	 * 
	 * @param ids
	 * @return key 項目ID，value 項目明細
	 */
	public abstract Map<Integer, Object> getReferences(Collection<Integer> ids);
	
	/**
	 * 查询最近的评论 
	 * @param space 空间
	 * @param limit 最大评论数
	 * @param queryPrivate 是否查询私人项目
	 * @param queryAdmin 是否查询管理员的回復
	 * @return
	 */
	public abstract List<Comment> queryLastComments(Space space,int limit,boolean queryPrivate,boolean queryAdmin);
	
	
	/**
	 * 获取某个项目的访问地址
	 * @param id
	 * @return
	 */
	public abstract Optional<String> getUrl(Integer id);
}
