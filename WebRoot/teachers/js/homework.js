'use strict';
var pageAddr = 1;
//新建点击交互
$('.new_top>a').click(function () {
    $('.new').css('display', 'block');
    $('.homework_main').css('display', 'none');
});
$('.btn_cancel_new').click(function () {
    $('.new').css('display', 'none');
    $('.homework_main').css('display', 'block');
});
$('.date_img_shang').click(function(){
    if($('#need_time').val() == ''){
        $('#need_time').val(1);
    }else{
        $('#need_time').val(parseInt($('#need_time').val()) + 1);
    }
});
$('.date_img_xia').click(function(){
    if(parseInt($('#need_time').val()) > 1){
        $('#need_time').val(parseInt($('#need_time').val()) - 1);
    }else{
        $('#need_time').val('');
    }
});
layui.use('laydate', function () {
    var laydate = layui.laydate;
    //多选完成时间
    laydate.render({
        elem: '#date_search',
        range: true
    });
    laydate.render({
        elem: '#test12',
        min: 0,
        trigger: 'click',
    });
});
$('#testTime').on('click',function (e) {
    e.stopPropagation();
    $('#test12').trigger('click')
});

//页面加载完毕进行页面渲染
$(function () {
    ///页面渲染
    $.ajax({
        url: host + 'pc/weixin_group/query/all',
        type: 'post',
        data: {},
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            var html = '';
            if (res.state == 0 && res.info.length > 0) {
                $('.main-content').css('display', 'block');
                $('.top_nav').css('display', 'block');
                $('.empty_null').css('display', 'none');
                for (var i in res.info) {
                    html += '<li class="top_nav_item">\n                                <a href="javascript:;" data-power="' + res.info[i].power + '" data-totalWork="' + res.info[i].totalWork + '" name="' + res.info[i].openGId + '" onclick="change_group(\'' + res.info[i].openGId + '\',\'' + res.info[i].power + '\',\'' + res.info[i].totalWork + '\')">' + res.info[i].groupName + '</a>\n                        </li>';
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
            $('#all_num').html($('.top_nav_list .top_nav_item a.active').attr('data-totalWork'));
            var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
            var url = host + 'pc/work/getCnt';
            getNum(openGID, url);
            var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
            if(power == 2){
                $('#selCheck').css('display','none');
            }else if(power == 3){
                $('#selCheck').css('display','inline-block');
            }
            var data = {
                beginDate: '',
                endDate: '',
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

//继续编辑
$('.main').on('click', '.btn_Continue_edit', function () {
    $('.new').css('display', 'block');
    $('.preview').css('display', 'none');
});
//新建点击交互
$('.new_top>a').click(function () {
    $('.new').css('display', 'block');
    $('.homework_main').css('display', 'none');
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    //科目渲染
    $.ajax({
        url: host + 'pc/subject/query',
        type: 'post',
        data: {
            openGId: openGID
        },
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            if (res.state == 0 && res.info.length > 0) {
                var html = '';
                for (var i = 0; i < res.info.length; i++) {
                    html += '<option value="' + res.info[i] + '">' + res.info[i] + '</option>';
                }
                $('#subject select').append(html);
                layui.use('form', function () {
                    var form = layui.form;
                    form.render();
                });
            }
        },
        error: function error() {
            layer.msg('科目加载失败！')
        }
    });
});
//离开新建页面
$('.main').on('click','#newBack',function(){
    layer.open({
        type: 1,
        title: ['温馨提示'],
        content: '<div style="padding:20px 35px;"><div style="width:40px;float:left;"><img style="width:100%;" src="images/assets/icon_login_fail.png" alt=""></div><p style="margin-left:50px;">您编辑的作业尚未保存，离开会使内容丢失。您确定要离开此页面吗？</p></div>',
        btn: ['保留在页面', '离开页面'],
        area: ['350px', '250px'],
        shadeClose: true,
        skin: 'layui-layer-molv',
        yes: function yes(index) {
            layer.close(index);
        },
        btn2: function btn2(index) {
            location.href = 'homework.html';
            layer.close(index);
        }
    });
});
//添加学科
$('.add_objs').click(function () {
    $('.add_object').css('display', 'block');
    $('.add_object .btn_confirm_add').click(function () {
        var subject = $('.object_input').val();
        var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
        var data = {
            subject: subject
        };
        $.ajax({
            url:  host + 'pc/subject/create',
            type: 'post',
            data: data,
            success: function success(res) {
                if(res.state==2 || res.state==1003 || res.state==1004){
                    location.href = host + 'login.html';
                }
                //console.log(res);
                if (res.state == 0 && res.message) {
                    data.subject = '';
                    $.ajax({
                        url: host + 'pc/subject/query',
                        type: 'post',
                        data: {
                            openGId: openGID
                        },
                        success: function success(res) {
                            if(res.state==2 || res.state==1003 || res.state==1004){
                                location.href = host + 'login.html';
                            }
                            //console.log(res);
                            if (res.state == 0 && res.info.length > 0) {
                                var html = '';
                                for (var i = 0; i < res.info.length; i++) {
                                    html += '<option value="' + res.info[i] + '">' + res.info[i] + '</option>';
                                }
                                $('#subject select').append(html);
                                layui.use('form', function () {
                                    var form = layui.form;
                                    form.render();
                                });
                            }
                        },
                        error: function error() {
                            layer.msg('科目添加失败！')
                        }
                    });
                }
            },
            error: function error() {
                layer.msg('科目添加失败！')
            }
        });
        $('.add_object').css('display', 'none');
        //科目渲染
    });
    $('.add_object .btn_add_cancel').click(function () {
        $('.add_object').css('display', 'none');
    });
});
//点击删除按钮显示弹框
function delete_a(id) {
    $('#' + id).find('.confirm_delete').css('display', 'block');
}
//点击确定提交参数，删除
function del(id) {
    var isSelf = $('#' + id).attr('data-flag');
    var url = host + 'pc/work/delete';
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var workID = [];
    workID.push(id);
    var data = {
        openGId: openGID,
        workId: workID
    };
    //console.log(data);
    deleteData(url,data,pageAddr);
    $('#' + id).find('.confirm_delete').css('display', 'none');
    $('#' + id).remove();
    $('#all_num').html(parseInt($('#all_num').html() - 1));
    if(isSelf == 1){
        $('#my_num').html(parseInt($('#my_num').html() - 1));
    }
}
//点击取消按钮取消操作
function cancel_del(id) {
    $('#' + id).find('.confirm_delete').css('display', 'none');
}
//删除全选弹窗，及批量删除及提交参数
function delete_all() {
    layer.open({
        type: 1,
        title: ['温馨提示'],
        content: '<div style="padding:30px;">您已选中多个作业，是否确认删除？</div>',
        btn: ['确认', '取消'],
        area: ['300px', '200px'],
        shadeClose: true,
        skin: 'layui-layer-molv',
        //回调函数
        yes: function yes(index) {
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'none');
            var select_checkbox = $('.same_table .checkbox_box :checkbox:checked');
            var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
            var workID = [];
            for (var i = 0; i < select_checkbox.length; i++) {
                var workId = parseInt(select_checkbox.eq(i).attr('name'));
                workID.push(workId);
            }
            var url = host + "pc/work/delete";
            var data = {
                openGId: openGID,
                workId: workID
            };
            //console.log(data);
            deleteData(url, data,pageAddr);
            layer.close(index);
            select_checkbox.parents('tr').remove();
            $('#all_num').html(parseInt($('#all_num').html() - workID.length));
            var numUrl = host + 'pc/work/getCnt';
            getNum(openGID, numUrl);
        },
        btn2: function btn2(index) {
            layer.close(index);
        }
    });
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

//点击不同群显示不同内容
function change_group(id, power, totalWork) {
    var beginData = $('#date_search').val().split(' - ')[0];
    var endData = $('#date_search').val().split(' - ')[1];
    var url = host + 'pc/work/getCnt';
    var openGID = id;
    var data = {
        beginDate: beginData,
        endDate: endData,
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        type: $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    $('#page_now').html(1);
    $('#all_num').html(totalWork);
    getNum(id, url);
    var renderType = 0;
    page(data, power,renderType);
}

//时间搜索
$('.date_img_box').click(function () {
    var beginData = $('#date_search').val().split(' - ')[0];
    var endData = $('#date_search').val().split(' - ')[1];
    var type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var data = {
        beginDate: beginData,
        endDate: endData,
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        type: type,
        perSize: 10
    };
    //console.log(data);
    $('#page_now').html(1);
    var renderType = 2;
    page(data, power,renderType);
});

///下拉框搜索
$('.top_sel .layui-anim,.layui-anim-upbit dd').click(function () {
    select_page(pageAddr);
});

//分页，及搜索
//数据渲染
function page(data, power,renderType) {
    //表格数据渲染
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            url: host + 'pc/work/query/page',
            data: data,
            type: 'post',
            success: function success(res) {
                if(res.state==2 || res.state==1003 || res.state==1004){
                    location.href = host + 'login.html';
                }
                //console.log(res);
                if (res.state == 0 && res.info.data.length > 0) {
                    $('.same_table').css('display', 'table');
                    $('.pagination_box').css('display', 'block');
                    $('.has_no_data').css('display', 'none');
                    var html = '';
                    for (var i in res.info.data) {
                        var imgList = '';
                        for (var r in res.info.data[i].files) {
                            imgList += '<img src="images/assets/img_' + res.info.data[i].files[r].fileFormat + '.png"/>';
                        }
                        if (power == 3) {
                            html += '<tr id="' + res.info.data[i].workId + '" data-flag="' + res.info.data[i].flag + '">\n                                    <td class="checkbox_box">\n                                        <input type="checkbox" name="' + res.info.data[i].workId + '" lay-skin="primary" lay-filter="itemChoose" title="">\n                                    </td>\n                                    <td class="work_name">\n                                        <p class="work_name">' + res.info.data[i].work.replace(/<br\/>/gi, " ") + '</p>\n                                    </td>\n                                    <td>\n                                        ' + imgList + '\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].name + '</td>\n                                    <td class="hue_gray">' + res.info.data[i].ctime + '</td>\n                                    <td>\n      <div class="tab_operate">\n                                 <a href="javascript:;" class="hue_green" onclick="show_detail(' + res.info.data[i].workId + ')">\u67E5\u770B\u8BE6\u60C5</a>\n                                        <a href="javascript:;" class="hue_red" onclick="delete_a(' + res.info.data[i].workId + ')">\u5220\u9664</a>\n                                        <div class="confirm_delete">\n                                            <div>\n                                                \u5220\u9664\u540E\u3001\u8001\u5E08/\u73ED\u4E3B\u4EFB\u5C06\u65E0\u6CD5\u67E5\u770B\u6B64\u4F5C\u4E1A\u7684\u8BE6\u60C5\uFF0C\u786E\u8BA4\u5220\u9664\u8BE5\u4F5C\u4E1A\uFF1F\n                                            </div>\n                                            <div>\n                                                <a href="javascript:;" class="btn_confirm_delete" onclick="del(' + res.info.data[i].workId + ')">\u786E\u8BA4</a>\n                                                <a href="javascript:;" class="btn_confirm_cancel" onclick="cancel_del(' + res.info.data[i].workId + ')">\u53D6\u6D88</a>\n                                            </div>\n                                        </div>\n                     </div>\n               </td>\n                                </tr>';
                        } else if (power == 2) {
                            html += '<tr id="' + res.info.data[i].workId + '"  data-flag="' + res.info.data[i].flag + '">\n                                    <td class="checkbox_box">\n                                        <input type="checkbox" name="' + res.info.data[i].workId + '" lay-skin="primary" lay-filter="itemChoose" title="">\n                                    </td>\n                                    <td class="work_name">\n                                        <p class="work_name">' + res.info.data[i].work.replace(/<br\/>/gi, " ") + '</p>\n                                    </td>\n                                    <td>\n                                        ' + imgList + '\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].name + '</td>\n                                    <td class="hue_gray">' + res.info.data[i].ctime + '</td>\n                                    <td>\n         <div class="tab_operate">\n                               <a href="javascript:;" class="hue_green" onclick="show_detail(' + res.info.data[i].workId + ')">\u67E5\u770B\u8BE6\u60C5</a>\n                                        <a href="javascript:;" ' + (res.info.data[i].flag == 1 ? 'style="display:inline-block"' : 'style="display:none"') + ' class="hue_red" onclick="delete_a(' + res.info.data[i].workId + ')">\u5220\u9664</a>\n                                        <div class="confirm_delete">\n                                            <div>\n                                                \u5220\u9664\u540E\u3001\u8001\u5E08/\u73ED\u4E3B\u4EFB\u5C06\u65E0\u6CD5\u67E5\u770B\u6B64\u4F5C\u4E1A\u7684\u8BE6\u60C5\uFF0C\u786E\u8BA4\u5220\u9664\u8BE5\u4F5C\u4E1A\uFF1F\n                                            </div>\n                                            <div>\n                                                <a href="javascript:;" class="btn_confirm_delete" onclick="del(' + res.info.data[i].workId + ')">\u786E\u8BA4</a>\n                                                <a href="javascript:;" class="btn_confirm_cancel" onclick="cancel_del(' + res.info.data[i].workId + ')">\u53D6\u6D88</a>\n                                            </div>\n                                        </div>\n                  </div>\n                  </td>\n                                </tr>';
                        }
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
                }else if(res.state == 0 && res.info.data.length == 0 && renderType == 0) {
                    //完全没有数据的页面样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                }else if(res.state == 0 && res.info.data.length == 0 && renderType == 1) {
                    //下拉渲染时没有数据的样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                    $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                    $('.has_no_data h5').html('该群内还没有该身份发布的作业！');
                } else if(res.state == 0 && res.info.data.length == 0 && renderType == 2) {
                    //按时间渲染时没有数据的样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                    $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                    $('.has_no_data h5').html('该群内还没有该时间段发布的作业！');
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
///上传
layui.use('upload',function(){
	$.post(host+"pc/files/getSign", {}, function(data){
		if(data.state==0){
			var appId = data.info.appId;
			var sign = data.info.sign;
			var timestamp = data.info.timestamp;
			var progressCodeImg = (Math.random() * (2100000000 + 1100000000)).toString();
		    var progressCodeFile = (Math.random() * (1100000000 + 1)).toString();
		    var dataImg = {
		        appId: appId,
		        sign: sign,
		        timestamp: timestamp,
		        progressCode: progressCodeImg,
		        purview:'public'
		    };
		    var dataFile = {
		        appId: appId,
		        sign: sign,
		        timestamp: timestamp,
		        progressCode: progressCodeFile,
		        purview:'private'
		    };
		    var upload = layui.upload; //得到 upload 对象
		    upload.render({
		        elem: '#homework_img',
		        method : 'post',
		        url: fileHost+'upload?appId=' + appId + '&sign=' + sign + '&timestamp=' + timestamp + '&progressCode=' + progressCodeImg +'&purview=public',
		        data:dataImg,
		        accept:'images',
		        exts:'jpg|png',
		        done: function(res){ //上传后的回调
		            //console.log(res);
		            $('.send_img_list').append('<li class="send_img_item">\n                                <img src="images/assets/icon_close.png" class="close_img" alt="">\n                                <div class="send_img_box" data-imgSize="'+res.size+'" data-imgName="' + fileName + '" data-imgAddr="' + res.message + '">\n                                    <img src="' + res.message + '" alt="">\n                                </div>\n                                <div class="mask_layer">\n                                    <img src="images/assets/icon_class_close_normal.png" alt="">\n                                    <div class="circleProgress_wrapper">\n                                        <div class="wrapper right">\n                                            <div class="circleProgress rightcircle"></div>\n                                        </div>\n                                        <div class="wrapper left">\n                                            <div class="circleProgress leftcircle"></div>\n                                        </div>\n                                    </div>\n                                </div>\n                                <div class="mask_layer1">\n                                    <p>\n                                        <i class="layui-icon">&#x1002;</i>\n                                    </p>\n                                    <p>\u91CD\u65B0\u4E0A\u4F20</p>\n                                </div>\n                            </li>');
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
		                        progressCode: progressCodeImg
		                    },
		                    success: function success(responseText) {
		                        var data = JSON.parse(responseText);
		                        if (data.state) {
		                            //前台更新进度
		                            //console.log(data);
		                            if (data.message.progress == data.message.totalSize) {
		                                clearInterval(ins);
		                                $('.mask_layer').css('display', 'none');
		                            } else if (prog == data.message.progress && count > 10) {
		                                clearInterval(ins);
		                                $('.mask_layer').css('display', 'block');
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
		    upload.render({
		        elem: '#homework_file',
		        method : 'post',
		        url:fileHost+'upload?appId=' + appId + '&sign=' + sign + '&timestamp=' + timestamp + '&progressCode=' + progressCodeFile +'&purview=private',
		        data:dataFile,
		        accept:'file',
		        exts:'doc|docx|xls|xlsx|pdf|ppt|pptx|mp4|jpg|png',
		        done: function(res){ //上传后的回调
		            //console.log(res);
		            $('.file_table').append('<tr data-fileName="' + fileName + '" data-fileSize="' + res.size + '" data-fileAddr="' + res.message + '">\n                                <td>\n                                    <img src="images/assets/img_pdf.png" alt="">\n                                    ' + fileName + '\n                                </td>\n                                <td class="progressSize">0.3M / changeSize(' + res.msg + ')</td>\n                                <td class="file_table_progress">\n                                    <div class="send_in">\n                                        <div style="width:160px">\n                                            <div class="layui-progress" lay-filter="demo" lay-showPercent="true">\n                                                <div class="layui-progress-bar" lay-percent="0%"></div>\n                                            </div>\n                                        </div>\n                                        <div>\n                                            <p><span style="color:#06C1AE;">1.00M/s</span>&nbsp;&nbsp;&nbsp;&nbsp;-\u5269\u4F59:0:00:10</p>\n                                        </div>\n                                    </div>\n                                    <div class="send_ok">\n                                        <img src="images/assets/icon_press_ok.png" alt="">\u4E0A\u4F20\u5B8C\u6210\n                                        <span class="costTime"></span>\n                                    </div>\n                                    <div class="send_fail">\n                                        <img src="images/assets/icon_press_fail.png" alt="">\u4E0A\u4F20\u5931\u8D25&nbsp;&nbsp;\n                                        <a href="javascript:;">\u91CD\u65B0\u4E0A\u4F20</a>\n                                    </div>\n                                </td>\n                                <td class="file_table_delete">\n                                    <a href="javascript:;">\u5220\u9664</a>\n                                </td>\n                            </tr>');
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
		                        progressCode: progressCodeFile
		                    },
		                    success: function success(responseText) {
		                        var data = JSON.parse(responseText);
		                        if (data.state) {
		                            //前台更新进度
		                            //console.log(data);
		                            layui.use('element', function () {
		                                var element = layui.element;
		                                element.progress('demo', data.message.progress / data.message.totalSize * 100 + '%');
		                            });
		                            $('.progressSize').html(changeSize(data.message.progress) + '/' + changeSize(data.message.progress));
		                            if (data.message.progress == data.message.totalSize) {
		                                clearInterval(ins);
		                                $('.progressSize').html(changeSize(data.message.totalSize));
		                                $('.send_ok').css('display', 'block');
		                                $('.send_in').css('display', 'none');
		                                $('.costTime').html(count * 0.1 + 's');
		                            } else if (prog == data.message.progress && count > 10) {
		                                clearInterval(ins);
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
	}, 'json');
});

//预览显示隐藏
$('.main').on('click', '.btn_preview_new', function () {
    var content = $('#textContent').val();
    var finalTime = $('#test12').val();
    if(content == ''){
        layer.msg('作业内容内容不能为空！');
    }else if(finalTime == ''){
        layer.msg('截止完成日期不能为空');
    }else{
        $('.new').css('display', 'none');
        $('.preview').css('display', 'block');
        var subject = $('#subject .layui-anim.layui-anim-upbit .layui-this').attr('lay-value');
        var name = $('.user_name>a').text();
        var myDate = new Date();
        var work_get = $('#textContent').val();
        var work = work_get.replace(/<(.+?)>/gi, "&lt;$1&gt;").replace(/ /gi, "&nbsp;").replace(/\n/gi, "<br/>");
        var imgElem = $('.send_img_item .send_img_box');
        var fileElem = $('.file_table tbody tr');
        var file = [];
        for (var i = 0; i < imgElem.length; i++) {
            var previewFile={};
            previewFile.fileS = imgElem.eq(i).attr('data-imgSize');
            previewFile.fileA = imgElem.eq(i).attr('data-imgAddr');
            previewFile.fileN = imgElem.eq(i).attr('data-imgName');
            previewFile.fileE = imgElem.eq(i).attr('data-imgName').substring(imgElem.eq(i).attr('data-imgName').lastIndexOf('.')+1 );
            file.push(previewFile);
        }
        for (var i = 0; i < fileElem.length; i++) {
            var previewFile={};
            previewFile.fileS = fileElem.eq(i).attr('data-fileSize');
            previewFile.fileA = fileElem.eq(i).attr('data-fileAddr');
            previewFile.fileN = fileElem.eq(i).attr('data-fileName');
            previewFile.fileE = fileElem.eq(i).attr('data-fileName').substring(fileElem.eq(i).attr('data-fileName').lastIndexOf('.')+1 );
            file.push(previewFile);
        }
        $('.preview_subject_title h1').html(subject + '作业详情');
        $('.preview_user_info>a').html(subject);
        $('.preview_user_info>span').html(myDate.toLocaleDateString());
        $('.preview_user_name').html(name);
        $('#work').html(work);
        $('#finish_time').html($('#test12').val());
        if($('#need_time').val() == ''){
            $('.work_time').css('display','none');
        }else{
            $('#work_time').html($('#need_time').val());
        }
        $('.file_num').html('('+file.length+')');
        var html = '';
        //console.log(file);
        for(var i = 0; i<file.length; i ++){
            html +="<li class='file_download_item'>"+
                "<div>"+
                "<div class='file_img_box'>"+
                "<img src='images/assets/img_"+file[i].fileE+"_bigger.png' alt=''>"+
                "</div>"+
                "<div class='file_detail'>"+
                "<p class='file_detail_name'>"+
                file[i].fileN+
                "<span>（"+changeSize(file[i].fileS)+"）</span>"+
                "</p>"+
                "<div>"+
                "<a href='javascript:;' class='detailPreview' data-addr='"+file[i].fileA+"'>预览</a>"+
                "</div>"+
                "</div>"+
                "</div>"+
                "</div>"
        }
        $('.file_download_list').html(html);
    }
});

///发送文件
$('.btn_release_new').click(function () {
    var content = $('#textContent').val();
    var finalTime = $('#test12').val();
    if(content == ''){
        layer.msg('作业内容内容不能为空！');
    }else if(finalTime == ''){
        layer.msg('截止完成日期不能为空');
    }else{
        send_work();
    }
});
$('.btn_confirm_release').click(function(){
    send_work();
});
//作业提交
function send_work() {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var subject = $('#subject .layui-anim.layui-anim-upbit .layui-this').attr('lay-value');
    var work_get = $('#textContent').val();
    var work = work_get.replace(/<(.+?)>/gi, "&lt;$1&gt;").replace(/ /gi, "&nbsp;").replace(/\n/gi, "<br/>");
    var files = [];
    var imgElem = $('.send_img_item .send_img_box');
    var fileElem = $('.file_table tbody tr');
    for (var i = 0; i < imgElem.length; i++) {
        var sendFile = {};
        sendFile.fileName = imgElem.eq(i).attr('data-imgName');
        sendFile.fileAddr = imgElem.eq(i).attr('data-imgAddr');
        files.push(sendFile);
    }
    for (var i = 0; i < fileElem.length; i++) {
        var sendFile = {};
        sendFile.fileName = fileElem.eq(i).attr('data-fileName');
        sendFile.fileAddr = fileElem.eq(i).attr('data-fileAddr');
        files.push(sendFile);
    }
    //console.log(files);
    var url = host + 'pc/work/create';
    var costTime = parseInt($('#need_time').val());
    var data = {
        costTime: costTime||0,
        files: files,
        openGId: openGID,
        subject: subject,
        submitTime: $('#test12').val(),
        work: work
    };
    //console.log(data);
    new_work(url, data);
}

//上传图片删除
$('.main').on('click', '.close_img', function () {
    $(this).parents('li.send_img_item').remove();
});
//上传文件删除
$('.main').on('click', '.file_table_delete a', function () {
    $(this).parents('tr').remove();
});

//发送新建作业数据
function new_work(url, data) {
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
                $('.new,.preview').css('display', 'none');
                $('.homework_main').css('display', 'block');
                var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
                var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
                var data = {
                    beginDate: '',
                    endDate: '',
                    openGId: openGID,
                    key: '',
                    page: 1,
                    type: 0,
                    perSize: 10
                };
                //console.log(data);
                page(data, power);
            }else{
                layer.msg(res.info);
            }
        },
        error: function error() {}
    });
}
//
//详情页面渲染
function show_detail(id) {
    $('.detail').css('display', 'block');
    $('.homework_main').css('display', 'none');
    $.ajax({
        url: host + 'pc/work/show',
        data: { workId: id },
        type: 'post',
        cache: false,
        dataType: 'json',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0) {
                if (res.info.files != null && res.info.files.length > 0) {
                    var file_html = '';
                    for (var i = 0; i < res.info.files.length; i++) {
                        file_html += '<li class="file_download_item">\n                                    <div>\n                                        <div class="file_img_box">\n                                            <img src="images/assets/img_' + res.info.files[i].fileFormat + '_bigger.png" alt="">\n                                        </div>\n                                        <div class="file_detail">\n                                            <p class="file_detail_name">\n                                                ' + res.info.files[i].fileName + '\n                                                <span>(' + changeSize(res.info.files[i].fileSize) + ')</span>\n                                            </p>\n                                            <div>\n                                                <a class="detailPreview" data-addr="' + res.info.files[i].fileAddr + '" href="javascript:;">\u9884\u89C8</a>\n                                                <a href="javscript:;" data-fileId="' + res.info.files[i].fileId + '" class="file_downLoad">\u4E0B\u8F7D</a>\n                                            </div>\n                                        </div>\n                                    </div>\n                                </li>';
                    }
                    var member_html = '';
                    for (var i = 0; i < res.info.readUserList.length; i++) {
                        member_html += '<li class="user_item">\n                                <div class="user_img">\n                                    <img src="' + res.info.readUserList[i].avatarUrl + '" alt="">\n                                </div>\n                                <div class="file_user_name">' + res.info.readUserList[i].aliasName + '</div>\n                            </li>';
                    }
                    $('.detail').html('<div class="new_file_top">\n                        <a href="homework.html">\n                            \u4F5C\u4E1A\u7BA1\u7406&nbsp;&nbsp;/&nbsp;&nbsp;\n                        </a>\n                        \u4F5C\u4E1A\u8BE6\u60C5\n                    </div>\n                    <div class="preview_subject_title">\n                        <h1>' + res.info.subject + '\u4F5C\u4E1A\u8BE6\u60C5</h1>\n                        <div class="preview_user_info">\n                            <a href="javascript:;">' + res.info.subject + '</a>&nbsp;&nbsp;\n                            <span class="preview_user_time">\n                            ' + res.info.ctime + '\n                        </span> \xB7\n                            <span class="preview_user_name" style="color:#FF8F1F"> ' + res.info.name + '</span>\n                        </div>\n                    </div>\n                    <div class="preview_homework_detail">\n                        <h4 class="text_title">\n                            <img src="images/assets/icon_ exhibition_document.png" alt="">\n                            \u4F5C\u4E1A\u5185\u5BB9\n                        </h4>\n                        <div class="text_detail">\n                            ' + res.info.work + '\n                        </div>\n                        <h4 class="text_title">\n                            <img src="images/assets/icon_ exhibition_time.png" alt="">\n                            \u622A\u6B62\u5B8C\u6210\u65F6\u95F4\n                        </h4>\n                        <h5 class="text_detail">' + res.info.submitTime + '</h5>\n                        <h4 class="text_title" ' + (res.info.costTime > 0 ? "style='display:block'" : "style='display:none'") + '>\n                            <img src="images/assets/icon_ exhibition_clock.png" alt="">\n                            \u8981\u6C42\u5B8C\u6210\u65F6\u95F4\n                        </h4>\n                        <h5 class="text_detail" ' + (res.info.costTime > 0 ? "style='display:block'" : "style='display:none'") + '>' + res.info.costTime + '\u5206</h5>\n                        <h4 class="text_title">\n                            <img src="images/assets/icon_ exhibition_floder.png" alt="">\n                            \u9644\u4EF6\u5185\u5BB9\n                            <span class="file_num">\uFF08' + res.info.files.length + '\uFF09&nbsp;&nbsp;|&nbsp;&nbsp;</span>\n                            <a class="file_download" href="javascript:;">\n                                <i class="layui-icon">&#xe601;</i>&nbsp;\u5168\u90E8\u4E0B\u8F7D\n                            </a>\n                        </h4>\n                        <div class="file_download_list_box">\n                            <ul class="file_download_list">' + file_html + ('</ul>\n                        </div>\n                        <div class="btn_group">\n                            <button class="btn_file_read btn_active" onclick="read_member(' + id + ')">\u5DF2\u8BFB\u4EBA\u6570(' + res.info.readCnt + ')</button>\n                            <button class="btn_file_unread" onclick="unread_member(' + id + ')">\u672A\u8BFB\u4EBA\u6570(' + res.info.notReadCnt + ')</button>\n                        </div>\n                        <ul class="user_list">') + member_html + '</ul>\n                    </div>');
                } else {
                    var member_html = '';
                    for (var i = 0; i < res.info.readUserList.length; i++) {
                        member_html += '<li class="user_item">\n                                <div class="user_img">\n                                    <img src="' + res.info.readUserList[i].avatarUrl + '" alt="">\n                                </div>\n                                <div class="file_user_name">' + res.info.readUserList[i].aliasName + '</div>\n                            </li>';
                    }
                    $('.detail').html('<div class="new_file_top">\n                        <a href="homework.html">\n                            \u4F5C\u4E1A\u7BA1\u7406&nbsp;&nbsp;/&nbsp;&nbsp;\n                        </a>\n                        \u4F5C\u4E1A\u8BE6\u60C5\n                    </div>\n                    <div class="preview_subject_title">\n                        <h1>' + res.info.subject + '\u4F5C\u4E1A\u8BE6\u60C5</h1>\n                        <div class="preview_user_info">\n                            <a href="javascript:;">' + res.info.subject + '</a>&nbsp;&nbsp;\n                            <span class="preview_user_time">\n                            ' + res.info.ctime + '\n                        </span> \xB7\n                            <span class="preview_user_name" style="color:#FF8F1F"> ' + res.info.name + '</span>\n                        </div>\n                    </div>\n                    <div class="preview_homework_detail">\n                        <h4 class="text_title">\n                            <img src="images/assets/icon_ exhibition_document.png" alt="">\n                            \u4F5C\u4E1A\u5185\u5BB9\n                        </h4>\n                        <div class="text_detail">\n                            ' + res.info.work + '\n                        </div>\n                        <h4 class="text_title">\n                            <img src="images/assets/icon_ exhibition_time.png" alt="">\n                            \u622A\u6B62\u5B8C\u6210\u65F6\u95F4\n                        </h4>\n                        <h5 class="text_detail">' + res.info.submitTime + '</h5>\n                        <h4 class="text_title" ' + (res.info.costTime > 0 ? "style='display:block'" : "style='display:none'") + '>\n                            <img src="images/assets/icon_ exhibition_clock.png" alt="">\n                            \u8981\u6C42\u5B8C\u6210\u65F6\u95F4\n                        </h4>\n                        <h5 class="text_detail" ' + (res.info.costTime > 0 ? "style='display:block'" : "style='display:none'") + '>' + res.info.costTime + '\u5206</h5>\n                        \n                        <div class="btn_group">\n                            <button class="btn_file_read btn_active" onclick="read_member(' + id + ')">\u5DF2\u8BFB\u4EBA\u6570(' + res.info.readCnt + ')</button>\n                            <button class="btn_file_unread" onclick="unread_member(' + id + ')">\u672A\u8BFB\u4EBA\u6570(' + res.info.notReadCnt + ')</button>\n                        </div>\n                        <ul class="user_list">' + member_html + '</ul>\n                    </div>');
                }
            }
        },
        error: function error(data) {}
    });
}
//详情页面文件预览
$('.main').on('click', '.detailPreview', function (e) {
    e.preventDefault();
    var fileAddr = $(this).attr('data-addr');
    var url = $(this).attr('data-addr').split('?')[0].split('.');
    var fileType = url[url.length - 1];
    previewFile(fileAddr,fileType);
});
//全部下载
$('.main').on('click', '.file_download', function (e) {
    e.preventDefault();
    var downList = $('.file_downLoad');
    for (var i = 0; i < downList.length; i++) {
        var fileId = downList.eq(i).attr('data-fileId');
        downloadFile(fileId);
    }
});
//单个下载
$('.main').on('click', '.file_downLoad', function (e) {
    e.preventDefault();
    console.log(1);
    var fileId = $(this).attr('data-fileId');
    downloadFile(fileId);
});

//已读人数列表
function read_member(id) {
    $('.btn_file_read').siblings().removeClass('btn_active');
    $('.btn_file_read').addClass('btn_active');
    $.ajax({
        url: host + 'pc/work/show',
        data: {
            workId: id
        },
        type: 'post',
        cache: false,
        dataType: 'json',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0) {
                var member_html = '';
                for (var i = 0; i < res.info.readUserList.length; i++) {
                    member_html += '<li class="user_item">\n                                <div class="user_img">\n                                    <img src="' + res.info.readUserList[i].avatarUrl + '" alt="">\n                                </div>\n                                <div class="file_user_name">' + res.info.readUserList[i].aliasName + '</div>\n                            </li>';
                }
                $('.user_list').html(member_html);
            }
        }
    });
}
//未读人数列表
function unread_member(id) {
    $('.btn_file_unread').siblings().removeClass('btn_active');
    $('.btn_file_unread').addClass('btn_active');
    $.ajax({
        url: host + 'pc/work/show',
        data: {
            workId: id
        },
        type: 'post',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0) {
                var member_html = '';
                for (var i = 0; i < res.info.notReadUserList.length; i++) {
                    member_html += '<li class="user_item">\n                                <div class="user_img">\n                                    <img src="' + res.info.notReadUserList[i].avatarUrl + '" alt="">\n                                </div>\n                                <div class="file_user_name">' + res.info.notReadUserList[i].aliasName + '</div>\n                            </li>';
                }
                $('.user_list').html(member_html);
            }
        }
    });
}

//文本输入框字数控制
$(document).ready(function(){
    var limitNum = 300;
    $('#leftSize').html(limitNum);
    $('#textContent').on('change input keyup keydown',function() {
        var remain = $(this).val().length;
        if (remain > 300) {
            $('#leftSize').html('0');
            $(this).val($(this).val().substring(0, 300));
            layer.msg('该区域最多可输入300字！');
        } else {
            var result = limitNum - remain;
            $('#leftSize').html(result);
        }
    });
    $('#need_time').on('keyup',function(){
        var Reg = /^[1-9]\d*$/;
        var isTrue = Reg.test(parseInt($(this).val()));
        if(!isTrue){
             layer.msg('请输入数字！')
         }
        $(this).val($(this).val().replace(/[^\d]+/,''));
    })
});