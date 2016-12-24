package me.qyh.blog.ui;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;

import com.google.common.collect.Maps;

import me.qyh.blog.exception.SystemException;
import me.qyh.blog.security.UserContext;
import me.qyh.blog.service.UIService;
import me.qyh.blog.ui.dialect.TransactionContext;
import me.qyh.blog.ui.page.ErrorPage;
import me.qyh.blog.ui.page.Page;
import me.qyh.blog.ui.page.UserPage;

public class PageReturnHandler extends RenderedSupport implements HandlerMethodReturnValueHandler {

	private static final Logger logger = LoggerFactory.getLogger(PageReturnHandler.class);

	@Autowired
	private UIService uiService;

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return Page.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		mavContainer.setRequestHandled(true);
		HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
		HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);

		Page page = (Page) returnValue;
		if (page == null) {
			throw new SystemException("不应该返回空页面");
		}
		if (Page.class.equals(page.getClass())) {
			throw new SystemException("必须返回一个确切的页面");
		}

		// find or create a view
		String templateName = TemplateUtils.getTemplateName(page);

		if (page instanceof UserPage && UserContext.get() == null) {
			// 如果是用户自定义页面，首先查询一次，因为自定义页面可能被锁保护
			uiService.queryPage(templateName);
		}
		try {
			String rendered = render(templateName, mavContainer.getModel(), nativeRequest, nativeResponse);
			Writer writer = nativeResponse.getWriter();
			writer.write(rendered);
			writer.flush();
		} catch (Exception e) {
			if (!nativeResponse.isCommitted()) {
				// 如果是错误页面发生了错误，不再跳转(防止死循环)
				if ((page instanceof ErrorPage)) {
					ErrorPage errorPage = (ErrorPage) page;
					logger.error("在错误页面" + errorPage.getErrorCode().name() + "发生了一个异常，为了防止死循环，这个页面发生异常将会无法跳转，异常栈信息:"
							+ e.getMessage(), e);
					try {
						// render /WEB-INF/templates/error/{errorCode}
						View errorView = thymeleafViewResolver
								.resolveViewName("error/" + errorPage.getErrorCode().getCode(), webRequest.getLocale());
						errorView.render(Maps.newHashMap(), nativeRequest, nativeResponse);
					} catch (Throwable e2) {
						// 不能够在这里继续抛出异常!
						logger.error("/WEB-INF/templates/error/500页面渲染异常！！！！！,异常信息:" + e.getMessage(), e);
					}
				} else {
					throw e;
				}
			} else {
				logger.error(e.getMessage(), e);
			}
		} finally {
			TransactionContext.remove();
		}
	}

}