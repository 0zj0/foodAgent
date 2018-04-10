'use strict';
var pageAddr = 4;
//页面表格渲染
$(function () {
    $.ajax({
        url: host + 'pc/weixin_group/query/all',
        type: 'post',
        data: {},
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            var html = '';
            //console.log(res);
            if (res.state == 0 && res.info.length > 0) {
                $('.main-content').css('display', 'block');
                $('.top_nav').css('display', 'block');
                $('.empty_null').css('display', 'none');
                for (var i in res.info) {
                    html += '<li class="top_nav_item">\n                                <a href="javascript:;" data-power="' + res.info[i].power + '" data-newLeave="' + res.info[i].newLeave + '" data-totalLeave="' + res.info[i].totalLeave + '" name="' + res.info[i].openGId + '" onclick="change_group(\'' + res.info[i].openGId + '\',\'' + res.info[i].totalLeave + '\' ,\'' + res.info[i].newLeave + '\' ,\'' + res.info[i].power + '\' )">' + res.info[i].groupName + '</a>\n                 <span'+ (res.info[i].power == 2 ? 'style="display:none"' : 'style="display:inline-block"') + '>               <span ' + (res.info[i].newLeave > 0 ? 'style="visibility:visible"' : 'style="visibility:hidden"') + ' class="new_leave_num">' + res.info[i].newLeave + '</span>\n         </span>\n               </li>';
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
            var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
            $('#all_num').html($('.top_nav_list .top_nav_item a.active').attr('data-totalLeave'));
            $('#my_num').html($('.top_nav_list .top_nav_item a.active').attr('data-newLeave'));
            var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
            if(power == 2){
                $('#hideStyle').css('display','none');
            }else if(power == 3){
                $('#hideStyle').css('display','inline-block');
            }
            //console.log(openGID);
            var data = {
                key: '',
                openGId: openGID,
                page: 1,
                perSize: 10,
                type: 0
            };
            var renderType = 0;
            page(data,renderType);
        },
        error:function(){
            $('.top_nav').css('display', 'none');
            $('.errorPage').css('display', 'block');
        }
    });
});
//点击显示隐藏文本
$('.main').on('click', '.leave_reason', function () {
    var text = $(this).text();
    layer.tips(text, $(this), {
        tips: [1, '#000'],
        time: 2000
    });
});
//同意与不同意
//同意
function agree_leave(id) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var num_add = $('#page_now').text();
    var pageData = {
        openGId: openGID,
        key: $('#leave_search').val(),
        page: 1,
        type: $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    var data = {
        auditState: 3,
        leaveId: id
    };
    $.ajax({
        url: host + 'pc/leave/audit',
        data: data,
        type: 'post',
        cache: false,
        dataType: 'json',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if(res.state == 0){
                page(pageData);
                $('.new_leave_num').html(parseInt($('.new_leave_num').html()) - 1);
                if (parseInt($('.new_leave_num').html()) < 1) {
                    $('.new_leave_num').css('display', 'none');
                    $('.new_msg_mark').css('display', 'none');
                } else {
                    $('.new_leave_num').css('display', 'block');
                }
            }else{
                layer.msg('审批失败！');
            }
        }
    });
}
//不同意
function disagree_leave(id) {
    var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
    var num_add = $('#page_now').text();
    var pageData = {
        openGId: openGID,
        key: $('#leave_search').val(),
        page: 1,
        type: $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    //console.log(data);
    page(data);
    var data = {
        auditState: 4,
        auditResult: $('#' + id).find('.leave_operate .input_leave_disagree').val(),
        leaveId: id
    };
    //console.log(data);
    $.ajax({
        url: host + 'pc/leave/audit',
        data: data,
        type: 'post',
        cache: false,
        dataType: 'json',
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            //console.log(res);
            if(res.state == 0){
                page(pageData);
                $('.new_leave_num').html(parseInt($('.new_leave_num').html()) - 1);
                if (parseInt($('.new_leave_num').html()) < 1) {
                    $('.new_leave_num').css('display', 'none');
                    $('.new_msg_mark').css('display', 'none');
                } else {
                    $('.new_leave_num').css('display', 'inline-block');
                }
            }else{
                layer.msg('审批失败！');
            }
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
function change_group(id, totalLeave,newLeave,power) {
    var openGID = id;
    var data = {
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        type: $('.top_sel .layui-form-select dl dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    $('#all_num').html(totalLeave);
    $('#my_num').html(newLeave);
    //console.log(data);
    var renderType = 0;
    page(data,renderType);
}
//下拉框搜索
$('.top_sel .layui-anim,.layui-anim-upbit dd').click(function () {
    select_page(pageAddr);
});

//分页，及搜索
function page(data,renderType) {
    //表格数据渲染
    $.ajax({
        url: host + 'pc/leave/query/page',
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
                    if (res.info.data[i].auditState == 1) {
                        html += '<tr id="' + res.info.data[i].leaveId + '">\n                                    <td>' + res.info.data[i].leaveName + '</td>\n                                    <td>\n                                        <a href="javascript:;" class="leave_pending">\u5F85\u5BA1\u6279</a>\n                                    </td>\n                                    <td class="hue_gray">\n                                        <p class="leave_reason">' + res.info.data[i].reason + '</p>\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].startTime + '\u81F3' + res.info.data[i].endTime + '</td>\n                                    <td>\n                                        <div class="leave_operate">\n                                            <a href="javascript:;" class="btn_leave_agree" onclick="agree_leave(' + res.info.data[i].leaveId + ')">\u540C\u610F</a>\n                                            <input type="text" class="input_leave_disagree" placeholder="\u62D2\u7EDD\u7406\u7531">\n                                            <a href="javascript:;" class="btn_leave_disagree" onclick="disagree_leave(' + res.info.data[i].leaveId + ')">\u4E0D\u540C\u610F</a>\n                                        </div>\n                                    </td>\n                                </tr>';
                    } else if (res.info.data[i].auditState == 2) {
                        html += '<tr id="' + res.info.data[i].leaveId + '">\n                                    <td>' + res.info.data[i].leaveName + '</td>\n                                    <td>\n                                        <a href="javascript:;" class="leave_pending">\u5BA1\u6279\u4E2D</a>\n                                    </td>\n                                    <td class="hue_gray">\n                                        <p class="leave_reason">' + res.info.data[i].reason + '</p>\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].startTime + '\u81F3' + res.info.data[i].endTime + '</td>\n                                    <td>\n                                        <div class="leave_operate">\n                                            \u6B63\u5728\u5BA1\u6279\u4E2D...\n                                        </div>\n                                    </td>\n                                </tr>';
                    } else if (res.info.data[i].auditState == 3) {
                        html += '<tr id="' + res.info.data[i].leaveId + '">\n                                    <td>' + res.info.data[i].leaveName + '</td>\n                                    <td>\n                                        <a href="javascript:;" class="leave_approve">\u5DF2\u5BA1\u6279</a>\n                                    </td>\n                                    <td class="hue_gray">\n                                        <p class="leave_reason">' + res.info.data[i].reason + '</p>\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].startTime + '\u81F3' + res.info.data[i].endTime + '</td>\n                                    <td>\n                                        <div class="leave_operate">\n                                            \u5DF2\u540C\u610F\u8BF7\u5047\n                                        </div>\n                                    </td>\n                                </tr>';
                    } else if (res.info.data[i].auditState == 4) {
                        if(res.info.data[i].auditResult != null){
                            html += '<tr id="' + res.info.data[i].leaveId + '">\n                                    <td>' + res.info.data[i].leaveName + '</td>\n                                    <td>\n                                        <a href="javascript:;" class="leave_approve">\u5DF2\u5BA1\u6279</a>\n                                    </td>\n                                    <td class="hue_gray">\n                                        <p class="leave_reason">' + res.info.data[i].reason + '</p>\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].startTime + '\u81F3' + res.info.data[i].endTime + '</td>\n                                    <td>\n                                        <div class="leave_operate">\n                                            \u4E0D\u540C\u610F\u8BF7\u5047,' + res.info.data[i].auditResult + '\n                                        </div>\n                                    </td>\n                                </tr>';
                        }else{
                            html += '<tr id="' + res.info.data[i].leaveId + '">\n                                    <td>' + res.info.data[i].leaveName + '</td>\n                                    <td>\n                                        <a href="javascript:;" class="leave_approve">\u5DF2\u5BA1\u6279</a>\n                                    </td>\n                                    <td class="hue_gray">\n                                        <p class="leave_reason">' + res.info.data[i].reason + '</p>\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].startTime + '\u81F3' + res.info.data[i].endTime + '</td>\n                                    <td>\n                                        <div class="leave_operate">\n                                            \u4E0D\u540C\u610F\u8BF7\u5047          \n                                        </div>\n                                    </td>\n                                </tr>';
                        }
                    }
                }
                $('.same_table tbody').html(html);
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
            }else if(res.state == 0 && res.info.data.length == 0 && renderType == 1) {
                //下拉渲染时没有数据的样式
                $('.same_table').css('display', 'none');
                $('.pagination_box').css('display', 'none');
                $('.has_no_data').css('display', 'block');
                $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                $('.has_no_data h5').html('该群内还没有该状态的请假审批！');
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
}

$.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
};

//# sourceMappingURL=leave_page-compiled.js.map

//# sourceMappingURL=leave_page-compiled-compiled.js.map