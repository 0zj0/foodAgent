"use strict";

var pageAddr = 5;

//成绩单备注修改
$('.remark').click(function () {
    var trs = $(".same_table input[type=checkbox]:checked").parents('tr');
    trs.find('.remark_detail>input').attr('disabled', false);
    trs.find('.remark_detail>input').css('borderColor', '#D5E5E7');
    trs.find('.btn_img').css('visibility', 'visible');
});
//点击显示隐藏文本
$('.remark').mouseover(function () {
    layer.tips('先选择一条/多条记录，再点击编辑图标，即可修改。', $(this), {
        tips: [1, '#000'],
        time: 2000
    });
});
//页面加载完毕进行页面渲染
$(function () {
    $.ajax({
        url: host + 'pc/weixin_group/query/all',
        type: 'post',
        data: {},
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            var html = '';
            if (res.state == 0 && res.info.length > 0) {
                $('.main-content').css('display', 'block');
                $('.top_nav').css('display', 'block');
                $('.empty_null').css('display', 'none');
                for (var i in res.info) {
                    html += "<li class=\"top_nav_item\">\n                                <a href=\"javascript:;\" data-power=\"" + res.info[i].power + "\" data-shareFile=\"" + res.info[i].totalShare + "\" name=\"" + res.info[i].openGId + "\" onclick=\"change_group('" + res.info[i].openGId + "','" + res.info[i].power + "','" + res.info[i].totalShare + "')\">" + res.info[i].groupName + "</a>\n                            </li>";
                }
                for (var i in res.info) {
                    if(res.info[i].newLeave > 0){
                        $('.new_msg_mark').css('display',"inline-block");
                        break;
                    }else{
                        $('.new_msg_mark').css('display',"none");
                    }
                }
                $('.top_nav_list').html(html);
                var param = $.getUrlParam('openGId');
                if(param == ''||param == null) {
                    $('.top_nav_item a').first().addClass('active');
                }else{
                    $('.top_nav_list .top_nav_item a[name='+param+']').addClass('active');
                }
            } else {
                $('.top_nav').css('display', 'none');
                $('.empty_null').css('display', 'block');
            }
            $('#all_num').html($('.top_nav_list .top_nav_item a.active').attr('data-shareFile'));
            var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
            var url = host + 'pc/share/getCnt';
            getNum(openGID, url);
            var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');

            //console.log(openGID);
            var data = {
                key: '',
                openGId: openGID,
                page: 1,
                perSize: 10
            };
            var renderType = 0;
            page(data, power,renderType);
        },
        error:function(){
            $('.top_nav').css('display', 'none');
            $('.errorPage').css('display', 'block');
        }
    });
});
//点击完成图标，发送参数，保存修改
$('.main').on('click', '.confirm_sendFile', function () {
    var fileName = $(this).attr('data-fileName');
    var fileUrl = $(this).attr('data-fileAddr');
    var remark = $(this).parents('.send_file_item').find('.shareFile_input').val();
    send_file_ok(fileName, fileUrl, remark);
    $(this).parents('.send_file_item').remove();
    $('#send_all_num').html(parseInt($('#send_all_num').html()) - 1);
    $('#send_in_num').html(parseInt($('#send_in_num').html()) - 1);
    if ($('.send_file_list .send_file_item').length == 0) {
        $('.send_file_box').css('display', 'none');
    }
});
//上传文件点击完成发送文件
function send_file_ok(fileName, fileUrl, remark) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var url = host + "pc/share/create";
    var data = {
        file: {
            fileName: fileName,
            fileAddr: fileUrl
        },
        openGId: openGID,
        remark: remark
    };
    var data1 = {
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        perSize: 10
    };
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        contentType: 'application/json',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0) {
                page(data1, power);
            }
        },
        error: function error() {}
    });
}
//点击删除按钮显示弹框
function delete_a(id) {
    $('#' + id).find('.confirm_delete').css('display', 'block');
}
//点击确定提交参数，删除
function del(id) {
    var isSelf = $('#' + id).attr('data-flag');
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var url = host + "pc/share/delete";
    var shareID = [];
    shareID.push(id);
    var data = {
        openGId: openGID,
        shareId: shareID
    };
    //console.log(data);
    deleteData(url, data,pageAddr);
    $('#' + id).find('.confirm_delete').css('display', 'none');
    $('#' + id).remove();
    $('#all_num').html(parseInt($('#all_num').html() - 1));
    if(isSelf == 1){
        $('#my_num').html(parseInt($('#my_num').html() - 1));
    }
}
//删除全选弹窗，及批量删除及提交参数
function delete_all() {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    layer.open({
        type: 1,
        title: ['温馨提示'],
        content: '<div style="padding:30px;">您已选中多个成员，是否确认删除？</div>',
        btn: ['确认', '取消'],
        area: ['300px', '200px'],
        shadeClose: true,
        skin: 'layui-layer-molv',
        //回调函数
        yes: function yes(index) {
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'none');
            var select_checkbox = $('.same_table .checkbox_box :checkbox:checked');
            var shareID = [];
            for (var i = 0; i < select_checkbox.length; i++) {
                var shareId = parseInt(select_checkbox.eq(i).attr('name'));
                shareID.push(shareId);
            }
            var url = host + "pc/share/delete";
            var data = {
                openGId: openGID,
                shareId: shareID
            };
            //console.log(data);
            deleteData(url, data,pageAddr);
            layer.close(index);
            select_checkbox.parents('tr').remove();
            $('#all_num').html(parseInt($('#all_num').html() - shareID.length));
        },
        btn2: function btn2(index) {
            layer.close(index);
        }
    });
}
//点击取消按钮取消操作
function cancel_del(id) {
    $('#' + id).find('.confirm_delete').css('display', 'none');
}

//点击完成图标，发送参数，保存修改
function edit_remark_ok(id) {
    var url = host + "pc/share/update";
    var data = {
        remark: $('#' + id).find('.remark_detail input').val(),
        shareId: id
    };
    //console.log(data);
    sendData(url, data);
    $('#' + id).find('.remark_detail input').attr('disabled', 'disabled');
    $('#' + id).find('.remark_detail input').css('border', '1px solid transparent');
    $('#' + id).find('.btn_img').css('visibility', 'hidden');
}
//分页查询下一页
$('.main').on('click','.btn_next',function(){
    next_page(pageAddr);
});
//分页查询上一页
$('.main').on('click','.btn_prev',function(){
    prev_page(pageAddr);
});
//分页跳转
$('.main').on('click','.btn_jump',function(){
    jump_page(pageAddr);
});
//分页搜索
$('.main').on('click','#input_search_btn',function(){
    search_page(pageAddr);
});
///下拉框搜索
$('.top_sel .layui-anim,.layui-anim-upbit dd').click(function () {
    select_page(pageAddr);
});

//点击不同群显示不同内容
function change_group(id, power, totalShare) {
    var url = host + 'pc/achievement/getCnt';
    getNum(id, url);
    var data = {
        openGId: id,
        key: $('#input_search').val(),
        page: 1,
        type: $('.layui-anim.layui-anim-upbit dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    $('#page_now').html(1);
    $('#all_num').html(totalShare);
    //console.log(data);
    var renderType = 0;
    page(data, power,renderType);
}
//分页，及搜索页面重新渲染
function page(data, power,renderType) {
    //表格数据渲染
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            url: host + "pc/share/query/page",
            data: data,
            type: 'post',
            cache: false,
            dataType: 'json',
            success: function success(res) {
                if(res.state==2 || res.state==1003 || res.state==1004){
                    location.href = host + 'login.html';
                }
               //console.log(res);
                var html = '';
                if (res.state == 0 && res.info.data.length > 0) {
                    $('.same_table').css('display', 'table');
                    $('.pagination_box').css('display', 'block');
                    $('.has_no_data').css('display', 'none');
                    for (var i in res.info.data) {
                        if (power == 3) {
                            html += "<tr id=\"" + res.info.data[i].shareId + "\" data-flag=\"" + res.info.data[i].flag + "\">\n                                    <td class=\"checkbox_box\">\n                                        <input type=\"checkbox\" name=\"" + res.info.data[i].shareId + "\" lay-skin=\"primary\" lay-filter=\"itemChoose\" title=\"\">\n                                    </td>\n                                    <td>\n              <p class=\"work_name\">                          <img src=\"images/assets/img_" + res.info.data[i].file.fileFormat + ".png\" alt=\"\">\n                                        " + res.info.data[i].file.fileName + "\n                   </p>                 </td>\n                                    <td>\n                                        " + changeSize(res.info.data[i].file.fileSize) + "\n                                    </td>\n                                    <td class=\"hue_gray\">" + res.info.data[i].name + "</td>\n                                    <td class=\"hue_gray\">" + res.info.data[i].ctime + "</td>\n                                    <td class=\"hue_gray remark_detail\">\n                                        <input type=\"text\" value=\"" + res.info.data[i].remark + "\" disabled>\n                                        <a href=\"javascript:;\" class=\"btn_img\" onclick=\"edit_remark_ok(" + res.info.data[i].shareId + ")\"></a>\n                                    </td>\n                                    <td>\n                    <div class=\"tab_operate\">                    <a href=\"javascript:;\" data-addr=\"" + res.info.data[i].file.fileAddr + "\" class=\"hue_green preview_file\" target=\"_blank\">\u9884\u89C8</a>\n                                        <a href=\"javascript:;\" data-fileId='" + res.info.data[i].file.fileId + "' class=\"hue_green file_download\" target=\"_blank\">下载</a>\n                                        <a href=\"javascript:;\" class=\"hue_red\" onclick=\"delete_a(" + res.info.data[i].shareId + ")\">\u5220\u9664</a>\n                                        <div class=\"confirm_delete\">\n                                            <div>\n                                                删除后，老师/班主任将无法查看共享文件的详情，确认删除该共享文件？                                            </div>\n                                            <div>\n                                                <a href=\"javascript:;\" class=\"btn_confirm_delete\" onclick=\"del(" + res.info.data[i].shareId + ")\">\u786E\u8BA4</a>\n                                                <a href=\"javascript:;\" class=\"btn_confirm_cancel\" onclick=\"cancel_del(" + res.info.data[i].shareId + ")\">\u53D6\u6D88</a>\n                                            </div>\n                                        </div>\n                     </div>               </td>\n                                </tr>";
                        } else if (power == 2) {
                            html += "<tr id=\"" + res.info.data[i].shareId + "\" data-flag=\" " + res.info.data[i].flag + "\">\n                                    <td class=\"checkbox_box\">\n                                        <input type=\"checkbox\" name=\"" + res.info.data[i].shareId + "\" lay-skin=\"primary\" lay-filter=\"itemChoose\" title=\"\" "+(!res.info.data[i].flag ? 'disabled' : '')+">\n                                    </td>\n                                    <td>\n               <p class=\"work_name\">                         <img src=\"images/assets/img_" + res.info.data[i].file.fileFormat + ".png\" alt=\"\">\n                                        " + res.info.data[i].file.fileName + "\n                 </p>                   </td>\n                                    <td>\n                                        " + changeSize(res.info.data[i].file.fileSize) + "\n                                    </td>\n                                    <td class=\"hue_gray\">" + res.info.data[i].name + "</td>\n                                    <td class=\"hue_gray\">" + res.info.data[i].ctime + "</td>\n                                    <td class=\"hue_gray remark_detail\">\n                                        <input type=\"text\" value=\"" + res.info.data[i].remark + "\" disabled>\n                                        <a href=\"javascript:;\" class=\"btn_img\" onclick=\"edit_remark_ok(" + res.info.data[i].shareId + ")\"></a>\n                                    </td>\n                                    <td>\n                     <div class=\"tab_operate\">                   <a href=\"javascript:;\" data-addr=\"" + res.info.data[i].file.fileAddr + "\" class=\"hue_green preview_file\" target=\"_blank\">\u9884\u89C8</a>\n                                        <a href=\"javascript:;\" data-fileId='" + res.info.data[i].file.fileId + "' class=\"hue_green file_download\" target=\"_blank\">下载</a>\n                                        <a href=\"javascript:;\" " + (res.info.data[i].flag == 1 ? 'style="display:inline-block"' : 'style="display:none"') + " class=\"hue_red\" onclick=\"delete_a(" + res.info.data[i].shareId + ")\">\u5220\u9664</a>\n                                        <div class=\"confirm_delete\">\n                                            <div>\n                                               删除后，老师/班主任将无法查看共享文件的详情，确认删除该共享文件？                                        </div>\n                                            <div>\n                                                <a href=\"javascript:;\" class=\"btn_confirm_delete\" onclick=\"del(" + res.info.data[i].shareId + ")\">\u786E\u8BA4</a>\n                                                <a href=\"javascript:;\" class=\"btn_confirm_cancel\" onclick=\"cancel_del(" + res.info.data[i].shareId + ")\">\u53D6\u6D88</a>\n                                            </div>\n                                        </div>\n                   </div>                 </td>\n                                </tr>";
                        } else {}
                    }
                    $('.same_table tbody').html(html);
                    form.render('checkbox');
                    $('#page_all').html(res.info.totalPage);
                    $('#page_now').html(parseInt(data.page));
                    if ($('#page_now').text() >= res.info.totalPage) {
                        $('.btn_next').css('visibility', "hidden");
                    } else {
                        $('.btn_next').css('visibility', "visible");
                    }
                    if ($('#page_now').text() <= 1) {
                        $('.btn_prev').css('visibility', "hidden");
                    } else {
                        $('.btn_prev').css('visibility', "visible");
                    }
                } else if(res.state == 0 && res.info.data.length == 0 && renderType == 0){
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                } else if(res.state == 0 && res.info.data.length == 0 && renderType == 1) {
                    //下拉渲染时没有数据的样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                    $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                    $('.has_no_data h5').html('该群内还没有该状态的共享文件！');
                }else if(res.state == 0 && res.info.data.length == 0 && renderType == 3 ) {
                    //搜索框渲染没有数据的样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                    $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                    $('.has_no_data h5').html('没有搜索到该关键字记录，换个关键字试试?');
                }
            },
            error: function error(data) {
                $('.same_table').css('display', 'none');
                $('.pagination_box').css('display', 'none');
                $('.has_no_data').css('display', 'block');
                $('.has_no_data img').attr('src','images/assets/error.png');
                $('.has_no_data h5').html('页面出错，请刷新重试！');
            }
        });
    });
}


var fileName = '';
var fileExts = '';
$('.main').on('change','.layui-upload-file',function(e){
    var src=e.target || window.event.srcElement; //获取事件源，兼容chrome/IE
    fileName = src.value.substring( src.value.lastIndexOf('\\')+1 );
    fileExts = src.value.substring( src.value.lastIndexOf('.')+1 );
});
//上传
layui.use('upload',function(){
	$.post(host+"pc/files/getSign", {}, function(data){
		if(data.state==0){
			var appId = data.info.appId;
			var sign = data.info.sign;
			var timestamp = data.info.timestamp;
			var progressCode = (Math.random() * (2100000000 + 1)).toString();
		    var url = fileHost+'upload?appId=' + appId + '&sign=' + sign + '&timestamp=' + timestamp + '&progressCode=' + progressCode + '&purview=private';
		    var data = {
		        appId: appId,
		        sign: sign,
		        timestamp: timestamp,
		        progressCode: progressCode
		    };
		    var upload = layui.upload; //得到 upload 对象
		    upload.render({
		        elem: '#send_file',
		        method : 'post',
		        url:url,
		        data:data,
		        accept:'file',
		        exts:'doc|docx|xls|xlsx|pdf|ppt|pptx|mp4|jpg|png',
		        field:'file',
		        done: function(res){ //上传后的回调
		           //console.log(res);
		            $('.send_file_box').css('display','block');
		            $('#send_all_num').html(parseInt($('#send_all_num').html()) + 1);
		            $('.send_file_list').append('<li class="send_file_item">\n                            <div class="progress_bg">\n                            </div>\n                            <div class="item_detail">\n                                <div class="shareFile_name">\n                                    <img src="images/assets/img_' + fileExts + '.png" alt="">\n                                    <span>' + fileName + '</span>\n                                </div>\n                                <div class="shareFile_size">\n                                  0  \n                                </div>\n                                <div class="shareFile_speed">\n                                    <div class="send_in">0</div>\n                                    <div class="send_ok">\n                                        <img src="images/assets/icon_press_ok.png" alt="">\u4E0A\u4F20\u5B8C\u6210\n                                    </div>\n                                    <div class="send_fail">\n                                        <img src="images/assets/icon_press_fail.png" alt="">\u4E0A\u4F20\u5931\u8D25\n                                    </div>\n                                </div>\n                                <div class="shareFile_remark">\n                                    <input class="shareFile_input" type="text" placeholder="\u8BF7\u8F93\u5165\u5907\u6CE8">\n                                </div>\n                                <div class="shareFile_operate">\n                                    <a href="javascript:;" class="confirm_sendFile" data-fileName="' + fileName + '" data-fileAddr="' + res.message + '">\u786E\u8BA4\u4E0A\u4F20</a>\n                                    <a href="javascript:;" class="hue_green sendAgain">\u91CD\u65B0\u4E0A\u4F20</a>\n                                    <a href="javascript:;" class="hue_red">\u5220\u9664</a>\n                                </div>\n                            </div>\n                        </li>');
		            var ins;
		            var count = 0;
		            var prog = 0;
		            function getProgress() {
		                count++;
		                $.ajax({
		                    url: fileHost+'progress',
		                    type: 'POST',
		                    dataType: 'text',
		                    data: {
		                        progressCode: progressCode
		                    },
		                    success: function success(responseText) {
		                        var data = JSON.parse(responseText);
		                        if (data.state) {
		                            //前台更新进度
		                           //console.log(data);
		                            $('.progress_bg').css('width', data.message.progress / data.message.totalSize * 100 + '%');
		                            $('.shareFile_size').html(changeSize(data.message.progress));
		                            if (data.message.progress == data.message.totalSize) {
		                                $('#send_in_num').html(parseInt($('#send_in_num').html()) + 1);
		                                clearInterval(ins);
		                                $('.confirm_sendFile').css('visibility', 'visible');
		                                $('.send_ok').css('display', 'block');
		                                $('.send_fail').css('display', 'none');
		                                $('.send_in').css('display', 'none');
		                            } else if (prog == data.message.progress && count > 10) {
		                                clearInterval(ins);
		                                $('.confirm_sendFile').css('visibility', 'hidden');
		                                $('.send_ok').css('display', 'none');
		                                $('.send_fail').css('display', 'block');
		                                $('.send_in').css('display', 'none');
		                            }
		                        }
		                    },
		                    error: function error(e) {
		                       //console.log(e);
		                    }
		                });
		            }
		            ins = setInterval(getProgress, 100);
		        },
		        size: 100*1024*1024 ,//最大允许上传的文件大小
		        multiple:false
		    });
		}
	});
});

//删除改上传列表
$('.send_file_list').on('click', '.shareFile_operate .hue_red', function () {
    if ($('.send_file_list .send_file_item').length > 1) {
        $(this).parents('.send_file_item').css('display', 'none');
        $('.send_file_box').css('display', 'block');
        $('#send_all_num').html(parseInt($('#send_all_num').html()) - 1);
        $('#send_in_num').html(parseInt($('#send_in_num').html()) - 1);
    } else {
        $(this).parents('.send_file_item').css('display', 'none');
        $('.send_file_box').css('display', 'none');
        $('#send_all_num').html(parseInt($('#send_all_num').html()) - 1);
        $('#send_in_num').html(parseInt($('#send_in_num').html()) - 1);
    }
});

//下载保存数据
$('.main').on('click','.tab_operate .file_download',function(e){
    e.preventDefault();
    var fileId = $(this).attr('data-fileId');
    downloadFile(fileId);
});


//预览文件
$('.main').on('click', '.tab_operate .preview_file', function (e) {
    e.preventDefault();
    var fileAddr = $(this).attr('data-addr');
   //console.log(fileAddr);
    var url = $(this).attr('data-addr').split('?')[0].split('.');
    var fileType = url[url.length - 1];
    previewFile(fileAddr,fileType);
});


//# sourceMappingURL=shareFile-compiled.js.map

//# sourceMappingURL=shareFile-compiled-compiled.js.map