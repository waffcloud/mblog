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
package me.qyh.blog.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import me.qyh.blog.core.config.Constants;
import me.qyh.blog.core.config.UrlHelper.SpaceUrls;
import me.qyh.blog.core.exception.SystemException;
import me.qyh.blog.core.message.Message;
import me.qyh.blog.core.util.ExceptionUtils;
import me.qyh.blog.core.util.Jsons;
import me.qyh.blog.core.util.Validators;
import me.qyh.blog.core.validator.SpaceValidator;
import me.qyh.blog.core.vo.JsonResult;

public class Webs {

	public static final String UNLOCK_ATTR_NAME = Webs.class.getName() + ".UNLOCK";
	public static final String ERROR_ATTR_NAME = Webs.class.getName() + ".ERROR";
	public static final String SPACE_ATTR_NAME = Webs.class.getName() + ".SPACE";
	public static final String SPACE_URLS_ATTR_NAME = Webs.class.getName() + ".SPACE_URLS";
	public static final String IP_ATTR_NAME = Webs.class.getName() + ".IP";

	/**
	 * tomcat client abort exception <br>
	 * 绝大部分不用记录这个异常，所以额外判断一下
	 */
	private static Class<?> clientAbortExceptionClass;

	static {
		try {
			clientAbortExceptionClass = Class.forName("org.apache.catalina.connector.ClientAbortException");
		} catch (ClassNotFoundException e) {
		}
	}

	private Webs() {

	}

	/**
	 * 是否是tomcat client abort exception
	 * 
	 * @param ex
	 * @return
	 */
	public static boolean isClientAbortException(Throwable ex) {
		return clientAbortExceptionClass != null
				&& ExceptionUtils.getFromChain(ex, clientAbortExceptionClass).isPresent();
	}

	/**
	 * 判断是否是ajax请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	/**
	 * 向响应中写入json信息
	 * 
	 * @param response
	 * @param result
	 * @throws IOException
	 */
	public static void writeInfo(HttpServletResponse response, JsonResult result) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(Constants.CHARSET.name());
		Jsons.write(result, response.getWriter());
	}

	/**
	 * 解码
	 * 
	 * @see URLDecoder#decode(String)
	 * @param toDecode
	 * @return
	 */
	public static String decode(String toDecode) {
		try {
			return URLDecoder.decode(toDecode, Constants.CHARSET.name());
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * 判断是否是解锁请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean unlockRequest(HttpServletRequest request) {
		Boolean isUnlock = (Boolean) request.getAttribute(UNLOCK_ATTR_NAME);
		return isUnlock != null && isUnlock;
	}

	/**
	 * 判断是否是错误页面请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean errorRequest(HttpServletRequest request) {
		Boolean isError = (Boolean) request.getAttribute(ERROR_ATTR_NAME);
		return isError != null && isError;
	}

	/**
	 * 保存上传的文件<br>
	 * <b>保存StandardMultipartFile.transferTo时存在异常</b>
	 * 
	 * @param mf
	 *            上传的文件
	 * @param file
	 *            保存的位置
	 * @throws IOException
	 */
	public static void save(MultipartFile mf, Path file) throws IOException {
		try (InputStream is = mf.getInputStream()) {
			Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * 从请求中获取space
	 * 
	 * @param request
	 * @return
	 */
	public static String getSpaceFromRequest(HttpServletRequest request) {
		String alias = (String) request.getAttribute(SPACE_ATTR_NAME);
		return alias == null ? null : alias.isEmpty() ? null : alias;
	}

	/**
	 * 从路径中获取space
	 * <p>
	 * space/ ==> null;<br>
	 * space/test ==> test<br>
	 * space ==> null<br>
	 * test ==> null<br>
	 * space/1234567891234567891234 ==> 123456789123456789123
	 * </p>
	 * <b> 仅负责从路径中获取空间别名，不负责校验空间别名是否合法 </b>
	 * 
	 * @see SpaceValidator#MAX_ALIAS_LENGTH
	 * @see SpaceValidator#isValidAlias(String)
	 * @param path
	 * @return
	 */
	public static String getSpaceFromPath(String path, int maxAliasLength) {
		if (maxAliasLength < 1) {
			throw new SystemException("maxAliasLength不能小于1");
		}
		if (Validators.isEmptyOrNull(path, true)) {
			return null;
		}
		if (!path.startsWith("space/")) {
			return null;
		}
		int length = path.length();
		if (length == 6) {
			return null;
		}
		int finalMaxAliasLength = maxAliasLength + 5;
		StringBuilder sb = new StringBuilder();
		for (int i = 6; i < length; i++) {
			char ch = path.charAt(i);
			if (ch == '/') {
				break;
			}
			sb.append(ch);
			if (i == finalMaxAliasLength) {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取请求中的SpaceUrls
	 * 
	 * @param request
	 * @return
	 */
	public static SpaceUrls getSpaceUrls(HttpServletRequest request) {
		return (SpaceUrls) request.getAttribute(SPACE_URLS_ATTR_NAME);
	}

	/**
	 * 获取请求中的IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIP(HttpServletRequest request) {
		return (String) request.getAttribute(IP_ATTR_NAME);
	}

	/**
	 * 从BindingResult中获取第一个错误，并且转化为JsonResult
	 * 
	 * @param result
	 * @return
	 */
	public static Optional<JsonResult> getFirstError(BindingResult result) {
		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for (ObjectError error : errors) {
				return Optional.of(new JsonResult(false,
						new Message(error.getCode(), error.getDefaultMessage(), error.getArguments())));
			}
		}
		return Optional.empty();
	}
}
