"use strict";

$(function () {
	/*//获取路径
	var pathName=window.document.location.pathname;
	//截取，得到项目名称
	var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	var base = window.location.host+projectName+"/";*/
	
    $.post(host+"pc/sign", function (data) {
    	console.log(data);
        if (data.state == 0) {
        	var connectWebSocket = function connectWebSocket() {
                if ("WebSocket" in window) {
                    ws = new WebSocket(socketHost + "ws/pc/login?sessionKey=" + sessionKey);
                } else if ("MozWebSocket" in window) {
                    ws = new MozWebSocket(socketHost + "sockjs/ws/pc/login?sessionKey=" + sessionKey);
                };
                ws.onopen = WSonOpen;
                ws.onmessage = WSonMessage;
                ws.onclose = WSonClose;
                ws.onerror = WSonError;
            };

            var WSonOpen = function WSonOpen() {
            	var count = 0;
            	var msgTimer = setInterval(function(){
            		if(count>=10000){
            			clearInterval(msgTimer);
            			msgTimer = null;
            		}else{
            			ws.send("");
            			count += 500;
            		}
            	}, 500);
            };

            var WSonMessage = function WSonMessage(msg) {
            	console.log(msg);
                msg = JSON.parse(msg.data);
                $('.mask_layer').css('display', 'block');
                var state_img = $('.mask_img img');
                var state_msg = $('#state_msg');
                var state_oper = $('#state_operate');
                //console.log(msg);
                var state = msg.state;
                if (state == 1006) {
                    //二维码已过期
                    state_img.attr('src', 'teachers/images/assets/icon_login_fail.png');
                    state_msg.text('二维码已失效');
                    state_oper.text('请点击刷新');
                    state_oper.attr('href', host+'login.html');
                }
                if (state == 6) {
                    //无权限访问
                    state_img.attr('src', 'teachers/images/assets/icon_login_fail.png');
                    state_msg.text('无权限访问');
                    state_oper.css('display', 'none');
                }
                if (state == 8) {
                    //已扫描
                    state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                    state_msg.text('以后扫描');
                    state_oper.css('display', 'none');
                }
                var reqCode = msg.reqCode;
                if (reqCode == 1001) {
                    //登陆
                    if (state == 1005) {
                    	//console.log("该账号在其它地方登录");
                    	//该账号在其它地方登录
                    	clearAllCookie();
                    	var date = new Date();
                        var year=date.getFullYear(); //获取当前年份
                        var mon=date.getMonth()+1; //获取当前月份
                        var day=date.getDate(); //获取当前日
                        var h=date.getHours(); //获取小时
                        var m=date.getMinutes(); //获取分钟
                        var s=date.getSeconds(); //获取秒
                        layer.alert('其他用户于'+year+'/'+mon+'/'+day+' '+h+':'+m+':'+s+'在其他地方登录，你已被迫下线，点击确认则回到登录页面', {
                            skin: 'layui-layer-molv' //样式类名
                            ,closeBtn: 0
                        }, function(){
                            setTimeout(function(){
                                window.location.href = host + "login.html";
                            }, 1000);
                        });

                        //state_img.attr('src', 'teachers/images/assets/icon_login_fail.png');
                        //state_msg.text('该账号在其他地方登录');
                        //state_oper.css('display', 'none');
                    }
                    if (state == 0) {
                        var type = msg.info.type;
                        console.log(type);
                        //登陆成功
                        $.post(host+"pc/login",function(res){
                        	console.log(res);
                            if(res.state == 0) {
                                if (type == 1) {
                                    //仅登陆
                                    state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                                    state_msg.text('登录成功');
                                    state_oper.css('display', 'none');
                                    location.href = host+'teachers/index.html';
                                }
                                if (type > 2) {
                                    //登陆并跳转
                                    var openGId = msg.info.openGId;
                                    //10：登录并布置作业；20：登录并发布班务通知；30：登录并查看成绩单；40：登录并查看共享文件；
                                    if (type == 10) {
                                        //登录并布置作业
                                        state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                                        state_msg.text('登录成功');
                                        state_oper.css('display', 'none');
                                        location.href = host+'teachers/homework.html?openGId='+openGId;
                                    }
                                    if (type == 20) {
                                        //登录并发布班务通知
                                        state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                                        state_msg.text('登录成功');
                                        state_oper.css('display', 'none');
                                        location.href = host+'teachers/classNotes.html?openGId='+openGId;
                                    }
                                    if (type == 30) {
                                        //登录并查看成绩单
                                        state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                                        state_msg.text('登录成功');
                                        state_oper.css('display', 'none');
                                        location.href = host+'teachers/transcript.html?openGId='+openGId;
                                    }
                                    if (type == 40) {
                                        //登录并查看共享文件
                                        state_img.attr('src', 'teachers/images/assets/icon_login_ok.png');
                                        state_msg.text('登录成功');
                                        state_oper.css('display', 'none');
                                        location.href = host+'teachers/shareFile.html?openGId='+openGId;
                                    }
                                }
                            }else{
                                state_img.attr('src', 'teachers/images/assets/icon_login_fail.png');
                                state_msg.text('登陆失败，刷新页面重试');
                                state_oper.css('display', 'none');
                            }
                        });
                    }
                }
                if (reqCode == 2) {
                	state_oper.text("文件已下载至电脑");
                    //下载
                    var fileAddrs = msg.info.fileAddrs;
                    for (var i = 0; i < fileAddrs.length; i++) {
                        $('body').prepend("<iframe class='downloadcsv' style=\"display:none\"></iframe>");
                        $('.downloadcsv:eq(0)').attr('src', fileAddrs[i]);
                    }
                }
            };

            var WSonClose = function WSonClose() {
                $('.mask_layer').css('display', 'block');
                var state_img = $('.mask_img img');
                var state_msg = $('#state_msg');
                var state_oper = $('#state_operate');
                state_img.attr('src', 'teachers/images/assets/icon_login_fail.png');
                state_msg.text('二维码过期');
                state_oper.text('请重新刷新页面');
            };

            var WSonError = function WSonError() {};
            
            var sessionKey;
            var ws;
            	
            var itv = setInterval(function(){
            	sessionKey = getCookie("pc_session_key_ticket_uname");
            	console.log("sessionKey:"+sessionKey);
            	if(sessionKey!=null){
            		clearInterval(itv);
            		connectWebSocket();
            	}
            }, 100);
        }
    }, "json");
});

//# sourceMappingURL=login-compiled.js.map