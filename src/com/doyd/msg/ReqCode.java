package com.doyd.msg;

public enum ReqCode {
	Unknown(0,"未知操作"),
	DownLoad(2, "下载", false),
	WebSocketCommon(90,"登录前WebSocket连接", false),
	WebSocketMajor(91,"登录后WebSocket连接"),
	WebSocketHeart(92,"WebSocket心跳"),
	WebSocket(93,"WebSocket http"),
	
	Common(1, "通用的"),
	

	/**
	 * 小程序
	 */
	Entrance(101,"小程序入口"),
	Init(102,"用户授权"),
	HomeInfo(103, "获取首页信息"),
	CreateChild(104, "添加孩子"),
	UpdateChild(105, "修改孩子"),
	ChildList(106, "获得孩子列表"),
	UserGroup(107, "获取用户所有群"),
	UpdateUser(109, "修改用户信息"),
	Subjects(110, "获得任教科目"),
	UpdateSubjects(111, "修改任教科目"),
	UserGroupAdd(112, "添加群关系"),
	UserGroupUnbind(113, "解绑群关系"),
	MsgRead(114, "阅读消息"),
	UserIdentify(115, "获得用户身份"),
	CreateUserIdentify(116, "创建用户身份"),
	GetOpenGId(117, "获得openGId"),
	Scan(118, "扫描二维码"),
	ScanConfirm(119, "扫描后确认"),
	BatchAddGroup(120, "批量添加群关系"),
	UpdateGroupName(121, "修改群名称"),
	GetSign(122, "上传文件获得签名"),
	GetFileAddr(122, "获得文件最新签名地址"),
	GetMessage(123, "获得消息列表"),
	
	Election(201, "纠正获得信息"),
	ElectionShow(202, "获得纠正信息"),
	CreateElection(203, "添加纠正信息"),
	CreateElectionLog(204, "添加纠正记录"),
	
	TransferShow(301, "获得转让信息"),
	CreateTransfer(302, "添加转让信息"),
	AcceptTransfer(303, "接受转让"),
	
	Share(401, "获得共享文件"),
	DeleteShare(402, "删除共享文件"),
	
	GroupUser(501, "获得群成员列表"),
	UpdateUserPhone(502, "修改用户电话"),
	DeleteUser(503, "删除群成员"),
	CreatePerfect(504, "添加完善通知"),
	User(505, "获得用户信息"),
	
	Achievement(601, "获得成绩单"),
	DeleteAchievement(602, "删除成绩单"),
	
	Leave(701, "获得请假审批"),
	CreateLeave(702, "添加请假审批"),
	UpdateLeave(703, "请假审批"),
	ReadLeave(704, "阅读请假"),
	
	Homework(801, "获得家庭作业"),
	HomeworkAdd(802, "添加家庭作业"),
	HomeworkShow(803, "获得家庭作业详情"),
	HomeworkRead(804, "获得家庭作业阅读记录"),
	HomeworkReadAdd(805, "添加家庭作业阅读记录"),
	HomeworkDelete(806, "删除家庭作业"),
	HomeworkNotify(807, "获得作业和班务"),
	
	Classnotify(901, "获得班务通知"),
	ClassnotifyAdd(902, "添加班务通知"),
	ClassnotifyShow(903, "获得班务通知详情"),
	ClassnotifyRead(904, "获得班务通知阅读记录"),
	ClassnotifyReadAdd(905, "添加班务通知阅读记录"),
	ClassnotifyDelete(906, "删除班务通知"),
	
	
	
	
	/**  pc端请求类型（1000~1100）    **/
	Login(1001, "登陆", false),
	Logout(1002, "注销"),
	Sign(1005, "签名", false),
	GetWeixinGroupList(1006, "查询微信群列表"),
	GetGroupUserList(1007, "查询群成员列表"),
	UpdateGroupUser(1008, "修改群成员"),
	DeleteGroupUser(1009, "删除群成员"),
	BatchDeleteGroupUser(1010, "批量删除群成员"),
	GetGroupUserCnt(1011, "查询群成员数量"),
	GetWorkList(1012, "查询作业列表"),
	ShowWork(1013, "查看作业详情"),
	CreateWork(1014, "布置作业"),
	DeleteWork(1015, "删除/批量删除作业"),
	GetWorkCnt(1016, "查询作业数量"),
	GetSubjectList(1017, "查询学科/任教科目"),
	CreateSubject(1018, "添加自定义学科"),
	GetClassNotifyList(1019, "查询班务通知列表"),
	ShowClassNotify(1020, "查看班务通知详情"),
	CreateClassNotify(1021, "发布班务通知"),
	DeleteClassNotify(1022, "删除/批量删除班务通知"),
	GetClassNotifyCnt(1023, "查询班务通知数量"),
	GetShareList(1024, "查询共享文件列表"),
	UpdateShare(1025, "修改共享文件"),
	CreateShare(1026, "新建共享文件"),
	DeleteShares(1027, "删除/批量删除共享文件"),
	GetShareCnt(1028, "查询共享文件数量"),
	GetAchievementList(1029, "查询成绩单列表"),
	UpdateAchievement(1030, "修改成绩单"),
	CreateAchievement(1031, "新建成绩单"),
	DeleteAchievements(1032, "删除/批量删除成绩单"),
	GetAchievementCnt(1033, "查询成绩单数量"),
	GetLeaveList(1032, "查询请假审批列表"),
	AuditLeave(1033, "审批请假申请"),
	GetLeaveCnt(1034, "查询请假审批数量"),
	Preview(1035, "预览"),
	
	;

	
	private int code;
	private String remark;
	
	private ReqCode(int code, String remark){
		this.code = code;
		this.remark = remark;
	}
	
	private ReqCode(int code, String remark, boolean needLogin){
		this.code = code;
		this.remark = remark;
	}

	public int getCode() {
		return code;
	}
	public String getRemark() {
		return remark;
	}

	public static ReqCode get(int code){
		for(ReqCode reqCode: ReqCode.values()){
			if(reqCode.getCode()==code){
				return reqCode;
			}
		}
		return Unknown;
	}
	
	@Override
	public String toString() {
		return this.code+"";
	}
}
