package com.doyd.msg;

public enum ReqState {
	Unknown(-1, "未知请求"),
	Success(0, "成功"),
	Failure(1, "失败"),
	SignatureFailed(2, "签名错误"),
	ApiParamError(3, "参数不完整或参数错误"),
	NoExistGroup(4, "不存在群"),
	NoExistUser(5, "用户不存在"),
	NoPower(6, "无权限访问"),
	NoRequestData(7, "无请求数据"),
	AlreadyScan(8, "已扫描"),
	WaitForLogin(9, "等待登录"),
	UserNotInGroup(10, "用户不在群中"),
	WrongUser(11, "错误的用户信息"),
	NoExistMember(12, "不存在的成员"),
	WrongPhone(13, "错误的电话"),
	NoExistWork(14, "作业不存在"),
	NoExistNotify(15, "班务通知不存在"),
	NoExistAchievement(16, "成绩单不存在"),
	NoExistLeave(17, "请假审批不存在"),
	NoExistShareFiles(18, "共享文件不存在"),
	NoExistFile(19, "文件不存在"),
	WrongTime(20, "错误的时间"),
	LackOpenGId(21, "缺少群id"),
	LackWorkId(22, "缺少作业id"),
	EmptySubject(23, "学科不能为空"),
	EmptyWork(24, "作业内容不能为空"),
	EmptySubmitTime(25, "提交作业时间不能为空"),
	SubmitTimeBeforeToday(26, "提交作业时间不能在今天之前"),
	OutOfContent(27, "内容超长"),
	LackUgId(28, "缺少群关系id"),
	WrongType(29, "错误的类型"),
	EmptyPhone(30, "电话号码不能为空"),
	LackFile(31, "缺少文件"),
	LackShareId(32, "缺少共享文件id"),
	EmptyRemark(33, "备注不能为空"),
	LackLeaveId(33, "缺少请假申请id"),
	WrongAuditState(34, "错误的审核状态"),
	EmptyAuditResult(35, "不同意情况下审核理由不能为空"),
	LackFileId(36, "缺少文件id"),
	LackNotifyId(37, "缺少班务通知id"),
	EmptyTitle(38, "标题不能为空"),
	EmptyContent(39, "内容不能为空"),
	LackScoreId(40, "缺少成绩单id"),
	ExistSubject(41, "已存在该学科"),
	WrongLink(42, "错误的链接"),
	NoAuthorized(43, "没有授权"),
	
	ExistNoPowerItem(99, "存在无权限的项"),
	
	/**    小程序状态类型    **/
	ExistChildren(101, "存在相同的孩子"),
	GroupExistDirector(102, "群内存在班主任"),
	UserExistElection(103, "用户存在纠正信息"),
	NoExistChild(104, "孩子不存在"),
	ExistUserGroup(105, "存在该班级关系"),
	UserExistVote(106, "用户存在投票信息"),
	GroupNoExistDirector(107, "群内不存在班主任"),
	NoExistMsg(108, "消息不存在"),
	EnterAgain(109, "被班主任踢出群，进入需要班主任同意方可再次进入"),
	ExistGroupName(110, "已有成员为此助手命名"),
	PowerRepeat(111, "你已经是该班群的班主任，拥有最高使用权限，无需再绑定成为任课老师"),
	ExistTeacherIdentity(112, "班主任身份拥有最高权限和所有功能，已为你合并该群老师身份"),
	
	
	RepeatVote(201, "重复投票"),
	ElectionEnd(202, "选举结束"),
	ElectionOutdate(202, "选举过时"),

	TransferEnd(301, "转让结束"),
	TransferOutdate(302, "转让过时"),
	NoTransferSelf(303, "不可以转移给自己"),
	
	
	
	
	
	
	
	
	/**  pc端状态类型（1001~1100）    **/
	SessionTimeOut(1003, "session time out"),
	SignTimeOut(1004, "sign time out"),
	CoverUser(1005, "该账号在其它地方登录"),
	QrcodeTimeOut(1006, "二维码已过期"),
	NoSupport(1007, "不支持"),
	;

    private int code;
	private String remark;
	
	private ReqState(int code, String remark){
		this.code = code;
		this.remark = remark;
	}

	public int getCode() {
		return code;
	}
	public String getRemark() {
		return remark;
	}
	public static ReqState get(int code){
		for(ReqState reqState: ReqState.values()){
			if(reqState.getCode()==code){
				return reqState;
			}
		}
		return Unknown;
	}
	@Override
	public String toString() {
		return this.code+"";
	}
}
