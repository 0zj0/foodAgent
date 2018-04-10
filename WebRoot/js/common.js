//公共的js交互

var url = document.location.href;
var http = url.split("://")[0];
var host = '',
    fileHost = '',
    socketHost = '';
if(http == 'http') {
    host += 'http://qun.wbgj.cn/jzqzs/';
    fileHost = 'https://img.fapiao.wbgj.cn/papers/';
    socketHost = 'ws://qun.wbgj.cn/jzqzs/';
}else if(http == 'https'){
    host += 'https://tla.wbgj.cn/jzqzs/';
    fileHost = 'https://tla.wbgj.cn/papers/';
    socketHost = 'wss://tla.wbgj.cn/jzqzs/';
}

    //头部导航样式切换
    $(".top_nav_more>a").click(function(){
        $(".top_nav_more").css('display','none');
        $('.top_nav_cancel').css('display','block');
        $('.top_nav_title').css('height',60);
        $('.top_nav_list').css({'overflow':"visible",'paddingRight':'0','height':'auto'});
    });
    $(".top_nav_cancel>a").click(function(){
        $(".top_nav_more").css('display','block');
        $('.top_nav_cancel').css('display','none');
        $('.top_nav_title').css('height',0);
        $('.top_nav_list').css({'overflow':"hidden",'paddingRight':'150px','height':'60px'});
    });
    $('.top_nav_list').on('click','.top_nav_item a',function(){
        $(this).parent('.top_nav_item').siblings().find('a').removeClass('active');
        $(this).addClass('active');
        $('.top_nav_list').prepend($(this).parents('.top_nav_item'));
    });


    //新建点击交互
    $('.new_top>a').click(function(){
        $('.new').css('display','block');
        $('.homework_main').css('display','none');
    });



//全选与全不选
layui.use('form',function(){
    var form = layui.form;
    form.on('checkbox(allChoose)', function(data){
        var hasChecked = data.othis.hasClass('layui-form-checked');
        var child = $('.same_table').find('.checkbox_box input[type="checkbox"]');
        console.log(child);
        if(hasChecked == true){
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'block');
            for (var i = 0; i < child.length; i++) {
                child.eq(i).addClass('layui-form-checked');
                $('.same_table tbody tr').css('background', '#F1F2F9');
                child.eq(i).prop({'checked': true});
            }
        }else{
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'none');
            for (var i = 0; i < child.length; i++) {
                child.eq(i).removeClass('layui-form-checked');
                $('.same_table tbody tr').css('background', '#fff');
                child.eq(i).prop({'checked': false});
            }
        }
        form.render('checkbox');
    });
    form.on('checkbox(itemChoose)',function(data){
        var sib = $('.same_table').find('.checkbox_box input[type="checkbox"]:checked').length;
        var total = $('.same_table').find('.checkbox_box input[type="checkbox"]').length;
        var hasChk = $(data.elem).prop('checked');
        if (hasChk === true) {
            $(data.elem).parents('tr').css('background', '#F1F2F9');
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'block');
        } else {
            $(data.elem).parents('tr').css('background', '#fff');
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'none');
        }
        if(sib == total){
            $('.sel_all input[type="checkbox"]').prop("checked",true);
            form.render();
        }else{
            $('.sel_all input[type="checkbox"]').prop("checked",false);
            form.render();
        }
        if (sib >= 1) {
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'block');
        } else {
            $('.top_sel .delete_box,.pagination_box .delete_box').css('display', 'none');
        }

    });
});



//鼠标悬停
$('.same_table').on('mouseover','tbody tr',function(){
    var checked=$(this).find('input[type=checkbox]').prop('checked');
    if(checked === true){
        $(this).css('background','#F1F2F9');
    }else{
        $(this).css('background','#F8F7F7');
    }
});
$('.same_table').on('mouseout','tbody tr',function(){
    var checked=$(this).find('input[type=checkbox]').prop('checked');
    if(checked === true){
        $(this).css('background','#F1F2F9');
    }else{
        $(this).css('background','#fff');
    }
});


//所有页面page跳转输入框输入验证
$('#page_input').on('keyup',function(){
    var maxNum = parseInt($('#page_all').html());
    var thisNum = parseInt($(this).val());
    var Reg = /^[1-9]\d*$/;
    var isTrue = Reg.test(parseInt($(this).val()));
    if(!isTrue){
        layer.msg('请输入数字！');
        $(this).val($(this).val().replace(/[^\d]+/,''));
    }else if(thisNum > maxNum){
        layer.msg('输入值不能大于总页数！');
        $(this).val('');
    }else{
        $(this).html(thisNum);
    }
});






//  1 代表布置作业主页面//  2 代表班务通知页面//  3 代表成绩单页面//  4 代表请假审批页面//  5 代表共享文件页面//  6 代表成员管理页面


////////////////////////删除/////////////////////

///删除发送数据
function deleteData(url,data,pageAddr) {
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            if(res.state == 0){
                var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
                var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
                var data = {
                    openGId: openGID,
                    type:$('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
                    key: $('#input_search').val(),
                    page: parseInt($('#page_now').html()),
                    perSize: 10
                };
                var url = '';
                if(pageAddr == 1){
                    data.beginDate = $('#date_search').val().split(' - ')[0];
                    data.endDate = $('#date_search').val().split(' - ')[1];
                    url = host + 'pc/work/getCnt';
                    getNum(openGID,url);
                    page(data,power);
                }else if(pageAddr == 2){
                    data.beginDate = $('#date_search').val().split(' - ')[0];
                    data.endDate = $('#date_search').val().split(' - ')[1];
                    url = host + 'pc/class_notify/getCnt';
                    getNum(openGID,url);
                    page(data,power);
                }else if(pageAddr == 3){
                    url = host + 'pc/achievement/getCnt';
                    getNum(openGID,url);
                    page(data,power);
                }
                else if(pageAddr == 5){
                    url = host + 'pc/share/getCnt';
                    getNum(openGID,url);
                    page(data,power);
                }
                else if(pageAddr == 6){
                    url = host + 'pc/weixin_user/getCnt';
                    getNum(openGID,url);
                    page(data,power);
                }
            }else{
                layer.msg('删除失败！')
            }
        },
        error: function error() {}
    });
}

//修改发送数据（编辑成绩单，共享文件备注，成员管理编辑手机号）

function  sendData(url, data) {
    $.ajax({
        url: url,
        type: 'post',
        data: data,
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0) {
                layer.msg('编辑成功！')
            }else {
                layer.msg('编辑失败！')
            }
        },
        error:function(){
            layer.msg('获取作业失败数量')
        }
    });
};

//获取我的数量
function  getNum(openGId, url) {
    $.ajax({
        url: url,
        type: 'post',
        data: {
            openGId: openGId,
            type: 1
        },
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            if (res.state == 0) {
                $('#my_num').html(res.info);
            }
        },
        error:function(){
            layer.msg('获取作业失败数量')
        }
    });
};

////////////////////////分页/////////////////////
//分页查询下一页
function next_page(pageAddr) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var data = {
        openGId: openGID,
        type:$('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        key: $('#input_search').val(),
        page: parseInt($('#page_now').html()) + 1,
        perSize: 10
    };
    if(pageAddr == 1||pageAddr == 2){
        data.beginDate = $('#date_search').val().split(' - ')[0];
        data.endDate = $('#date_search').val().split(' - ')[1];
        page(data,power);
    }else if(pageAddr == 3 || pageAddr == 4||pageAddr == 5 || pageAddr == 6){
        data.type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
        page(data,power);
    }
}
//分页查询上一页
function prev_page(pageAddr) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var data = {
        openGId: openGID,
        type:$('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        key: $('#input_search').val(),
        page: parseInt($('#page_now').html()) - 1,
        perSize: 10
    };
    if(pageAddr == 1 || pageAddr == 2){
        data.type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
        data.beginDate = $('#date_search').val().split(' - ')[0];
        data.endDate = $('#date_search').val().split(' - ')[1];
        console.log(data);
        page(data,power);
    }else if(pageAddr == 3 || pageAddr == 4||pageAddr == 5 || pageAddr == 6){
        data.type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
        page(data,power);
    }
}

//分页跳转
function jump_page(pageAddr) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var num_input = $('#page_input').val();
    var data = {
        openGId: openGID,
        type:$('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        key: $('#input_search').val(),
        page: parseInt(num_input),
        perSize: 10
    };
    if (num_input == '') {
        layer.msg('输入页数不能为空！')
    } else {
        if(pageAddr == 1 || pageAddr == 2){
            data.beginDate = $('#date_search').val().split(' - ')[0];
            data.endDate = $('#date_search').val().split(' - ')[1];
            page(data,power);
        }else if(pageAddr == 3 || pageAddr == 4||pageAddr == 5 || pageAddr == 6){
            page(data,power);
        }
    }
}

//输入框搜索
function search_page(pageAddr) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var data = {
        openGId: openGID,
        type:$('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        key: $('#input_search').val(),
        page: 1,
        perSize: 10
    };
    if(pageAddr == 1 || pageAddr == 2){
        data.beginDate = $('#date_search').val().split(' - ')[0];
        data.endDate = $('#date_search').val().split(' - ')[1];
        page(data,power);
    }else if(pageAddr == 3 || pageAddr == 4||pageAddr == 5 || pageAddr == 6){
        data.type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
        page(data,power);
    }
}

//下拉框搜索
function select_page(pageAddr) {
    var type = $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value');
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
    var renderType = 1;
    var data = {
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        type: type,
        perSize: 10
    };
    if(pageAddr == 1 || pageAddr == 2){
        data.beginDate = $('#date_search').val().split(' - ')[0];
        data.endDate = $('#date_search').val().split(' - ')[1];
        page(data,power,renderType);
    }else if(pageAddr == 4){
        page(data,renderType);
    }else if(pageAddr == 3 || pageAddr == 5 || pageAddr == 6){
        page(data,power,renderType);
    }
}

//下载//
//下载保存数据
function downloadFile(fileId) {
    $.ajax({
        type: 'POST',
        url: host + 'pc/files/download',
        data: {fileId:fileId},
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            if(res.state == 0){
                $('body').prepend("<iframe class='downloadcsv' style=\"display:none\"></iframe>");
                $('.downloadcsv:eq(0)').attr('src', res.info);
            }else{
                layer.msg(res.message);
            }
        },
        dataType: "json"
    });
}
//文件预览//
function previewFile(url,fileType){
    if (fileType == 'png' || fileType == 'jpg'||fileType == 'mp4'||fileType == 'pdf') {
        window.open(url);
    } else if (fileType == 'doc' || fileType == 'docx' || fileType == 'xls' || fileType == 'xlsx' || fileType == 'ppt' || fileType == 'pptx') {
        window.open(host + 'pc/files/preview?fileAddr=' + url);
    } else {
        layer.open({
            type: 1,
            area: ['300px', '200px'], //宽高
            content: '<p style="margin-top:60px;text-align: center;color:#FF1B5D">文件格式错误！</p>'
        });
    }
}
$(function(){
    var userName = getCookie('realName');
    var userImg = getCookie('avatarUrl');
    userImg = userImg.replace(/\"/g,"");
    console.log(userImg);
    $('#user_name').html(userName);
    $('#user_img').attr('src',userImg);
});
function getCookie(c_name) {
    if(document.cookie.length > 0) {
        c_start = document.cookie.indexOf(c_name + "=");
        if(c_start != -1) {
            c_start = c_start + c_name.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if(c_end == -1) c_end = document.cookie.length;
            return decodeURI(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}
function clearAllCookie() {  
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);  
    if(keys) {  
        for(var i = keys.length; i--;)  
            document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()  
    }  
}  
//退出登录
$('body').on('click','#logout',function(){
    $.ajax({
        type: 'POST',
        url: host + 'pc/logout',
        data: {},
        success: function success(res) {
            location.href = host + 'login.html';
        }
    });
});

//获取页面路径的参数
$.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
};

////字节大小转化
function changeSize(size) {
    var fileSize = '';
    if (size > 1024 * 1024 * 1024) {
        fileSize += (size / (1024 * 1024 * 1024)).toFixed(1) + 'GB';
    } else if (size >= 1024 * 1024) {
        fileSize += (size / (1024 * 1024)).toFixed(1) + 'MB';
    } else if (size >= 1024) {
        fileSize += (size / 1024).toFixed(0) + 'KB';
    } else {
        fileSize += size.toFixed(0) + 'B';
    }
    return fileSize;
}



