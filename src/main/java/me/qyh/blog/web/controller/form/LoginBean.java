package me.qyh.blog.web.controller.form;

public class LoginBean {

	private String username;
	private String password;
	private String validateCode;
	private boolean rememberMe;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public LoginBean() {

	}

	public LoginBean(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
