package com.doyd.core.convertor;


public class AppConfig {
	private int cookieTimeout = 0;//默认cookie有效时间，秒数
	private boolean debug; // 调试模式
	private String reflushSecret = "doyd123456"; // 刷新缓存密钥
	
	private String redisHost = "localhost"; // redis服务器
	private int redisPort = 6379; // redis服务器端口
	private String redisPassword = null; // redis服务器密码
	
	private String appletAppId; // 小程序appId
	private String appletGhId; // 小程序ghId
	
	private String appletCustomerSecret; // 小程序客户端签名密钥
	
	private String pcLoginSecret = "JWylSb44";// pc端登陆签名秘钥
	
	private String appletDomain = "http://act.doyd.cn";//小程序拦截域名
	private String appletPath = "pc/login";//小程序拦截路径
	
	private String papersGetCodeUrl = ""; // 文件服务器获得访问码路径
	private String papersAppId = ""; // 文件服务器appId
	private String papersSecret = ""; // 文件服务器密钥
	private String papersUploadUrl = ""; // 文件服务器上传接口
	
	private String pcUrl; // pc端登录页面路径
	
	public int getCookieTimeout() {
		return cookieTimeout;
	}
	public void setCookieTimeout(int cookieTimeout) {
		this.cookieTimeout = cookieTimeout;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public String getReflushSecret() {
		return reflushSecret;
	}
	public void setReflushSecret(String reflushSecret) {
		this.reflushSecret = reflushSecret;
	}
	public String getRedisHost() {
		return redisHost;
	}
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}
	public int getRedisPort() {
		return redisPort;
	}
	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}
	public String getRedisPassword() {
		return redisPassword;
	}
	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}
	public String getAppletAppId() {
		return appletAppId;
	}
	public void setAppletAppId(String appletAppId) {
		this.appletAppId = appletAppId;
	}
	public String getAppletGhId() {
		return appletGhId;
	}
	public void setAppletGhId(String appletGhId) {
		this.appletGhId = appletGhId;
	}
	public String getAppletCustomerSecret() {
		return appletCustomerSecret;
	}
	public void setAppletCustomerSecret(String appletCustomerSecret) {
		this.appletCustomerSecret = appletCustomerSecret;
	}
	public String getPcLoginSecret() {
		return pcLoginSecret;
	}
	public void setPcLoginSecret(String pcLoginSecret) {
		this.pcLoginSecret = pcLoginSecret;
	}
	public String getAppletDomain() {
		return appletDomain;
	}
	public void setAppletDomain(String appletDomain) {
		this.appletDomain = appletDomain;
	}
	public String getAppletPath() {
		return appletPath;
	}
	public void setAppletPath(String appletPath) {
		this.appletPath = appletPath;
	}
	public String getPapersGetCodeUrl() {
		return papersGetCodeUrl;
	}
	public void setPapersGetCodeUrl(String papersGetCodeUrl) {
		this.papersGetCodeUrl = papersGetCodeUrl;
	}
	public String getPapersAppId() {
		return papersAppId;
	}
	public void setPapersAppId(String papersAppId) {
		this.papersAppId = papersAppId;
	}
	public String getPapersSecret() {
		return papersSecret;
	}
	public void setPapersSecret(String papersSecret) {
		this.papersSecret = papersSecret;
	}
	public String getPapersUploadUrl() {
		return papersUploadUrl;
	}
	public void setPapersUploadUrl(String papersUploadUrl) {
		this.papersUploadUrl = papersUploadUrl;
	}
	public String getPcUrl() {
		return pcUrl;
	}
	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}
}
