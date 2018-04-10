package com.doyd.core;


public class CoreVars {
	/**
	 * 小程序用户
	 */
	public static final String APPLET_USER = "appletUser";
	
	/**
	 * ajax用户需要的参数
	 */
	public static final String AJAX_USER = "ajax_user";
	
	/**
	 * 系统管理员帐号
	 */
	public static final String ADMIN_NAME = "admin";
	
	/**
	 * 当前登录用户
	 */
	public static final String CURRENT_USER = "csu";
	
	/**
	 * 页面的basePath标签
	 */
	public static final String BASE_PATH = "basePath";
	
	/**
	 * 上方导航栏
	 */
	public static final String NAV_BAR = "navBar";
	
	/**
	 * 面包屑
	 */
	public static final String NAV_BREADCRUMB = "bnav";
	
	/**
	 * 当前菜单
	 */
	public static final String MENU_CURRENT = "curMenu";
	
	
	public static final String ANCESTOR_RIGHTS = "aRights";
	
	/**
	 * spring mvc拦截的后缀
	 */
	public static final String SUFFIX = "((.json)|(.xml))";
	/**
	 * 无需登录就可访问的路径
	 */
	public static final String ANONYMOUS_ACCESS_PATH = "^(index|login|logout|ssologin|authorize|autoreply|(api(/.+)+)|(flush(/.+)*)|(reflush(/.+)*)|(nginx(/.+)*))"+SUFFIX.replace(".", "\\.")+"?$";
	
	/**
	 * 登陆超时、没有登录的标记
	 */
	public static final String NO_LOGIN_TAG = "session timeout";
	
	/************************ 正则表达式> ****************************************
	 * 正则表达式规则 rules
	 ***********************/
	
	
	/**数字、字母、下划线*/
	public static final String RULES_UID = "[a-zA-Z0-9_]+";
	
	/**中文、数字、字母、下划线*/
	public static final String RULES_UNAME = "[\\u4E00-\\u9FA5\\w]+";
	
	/**用户登录名只能输入数字、字母、下划线、@、点号*/
	public static final String RULES_LOGIN_NAME = "[\\w\\.\\-]+(@\\w+(\\.\\w+)*)?";
	
	/**数字*/
	public static final String RULES_NUMBER = "\\-?\\d+(\\.\\d+)?";
	
	/**整数*/
	public static final String RULES_INT = "\\-?\\d+";
	
	/**正整数*/
	public static final String RULES_DIGITS = "\\d+";
	
	/**货币数字*/
	public static final String RULES_MONEY = "\\d+(\\.\\d{1,2})?";
	
	/**邮箱*/
	public static final String RULES_EMAIL = "[\\w\\.\\-_]+@[\\w\\-_]+(\\.[\\w\\-_]+)*";
	
	/**身份证*/
	public static final String RULES_IDCARD = "(^\\d{18}$)|(^\\d{15}$)";
	
	/**url地址*/
	public static final String RULES_URL = "(http(s?)|ftp)://(([\\w\\-]+\\.)+[\\w]+|localhost)(:\\d+)?" // 解析域名
										+ "(/([\\w\\-\\._,#%=\\*\\{\\}\\[\\]])*)*" // 解析uri
										+ "[\\?&#]?([\\w\\.\\+\\-!=#,_%&\\*\\{\\}\\[\\]]*)*";// 解析参数
	
	/**uri地址*/
	public static final String RULES_URI = "("+RULES_URL+")|"
										+ "(([\\w\\-\\._,#%=/\\*\\{\\}\\[\\]])+" // 解析uri
										+ "[\\?&#]?([\\w\\.\\+\\-!=#,_%&\\*\\{\\}\\[\\]]*)*)";// 解析参数
	
	/**时间*/
	public static final String RULES_TIME = "([01][0-9]|2[0-3]):([0-5][0-9])(:([0-5][0-9]))?";
	
	/**日期*/
	public static final String RULES_DATA = "\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[0-1])( " +RULES_TIME + ")?";
	
	/**完整的日期格式*/
	public static final String RULES_DATA_FULL = "\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[0-1]) ([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])";
	
	/**手机号码*/
	public static final String RULES_MOBILE = "(\\+?86)?(1[34578]{1}[0-9]{9})";
	
	/**座机*/
	public static final String RULES_PHONE = "([0-9]{3,4}\\-?)?[0-9]{7,8}";
	
	/**手机号码*/
	public static final String RULES_TELEPHONE = "(\\d+\\-)*\\d+";
	
	/**IP*/
	public static final String RULES_IP = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
	
	/**微信账号*/
	public static final String RULES_WEIXIN_NAME = "^[a-zA-Z\\d_]{5,}$";
	/************************ <正则表达式 ****************************************/
	
	/************************ 其他系统参数 ****************************************/
	/**COOKIE 过期时间，单位秒*/
	public static final String COOKIE_TIMEOUT = "cookie.timeout";
	
}
