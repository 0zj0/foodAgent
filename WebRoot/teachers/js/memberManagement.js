'use strict';

var pageAddr = 6;


//页面表格渲染
$(function () {
    $.ajax({
        url: host + '/pc/weixin_group/query/all',
        type: 'post',
        data: {},
        success: function success(res) {
            if(res.state==2 || res.state==1003 || res.state==1004){
                location.href = host + 'login.html';
            }
            if (res.state == 0 && res.info.length > 0) {
                $('.top_nav').css('display', 'block');
                $('.main-content').css('display', 'block');
                $('.empty_null').css('display', 'none');
                var html = '';
                for (var i in res.info) {
                    html += '<li class="top_nav_item">\n                                <a href="javascript:;" data-power="' + res.info[i].power + '" data-member="' + res.info[i].peopleCnt + '" name="' + res.info[i].openGId + '" onclick="change_group(\'' + res.info[i].openGId + '\',\'' + res.info[i].power + '\',\'' + res.info[i].peopleCnt + '\')">' + res.info[i].groupName + '</a>\n                        </li>';
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
            $('#all_num').html($('.top_nav_list .top_nav_item a.active').attr('data-member'));
            var url = host + 'pc/weixin_user/getCnt';
            var openGID = $('.top_nav_list .top_nav_item a.active').attr('name');
            var power = $('.top_nav_list .top_nav_item a.active').attr('data-power');
            //console.log(power);
            getNum(openGID, url);
            var data = {
                key: '',
                openGId: openGID,
                page: 1,
                perSize: 10,
                type: 0
            };
            var renderType = 0;
            page(data, power,renderType);
        },
        error:function (){
            $('.top_nav').css('display', 'none');
            $('.errorPage').css('display', 'block');
        }
    });
});
//点击删除按钮显示弹框
function delete_a(id) {
    $('#' + id).find('.confirm_delete').css('display', 'block');
}
//点击确定提交参数，删除
function del(id) {
    var url = host + '/pc/weixin_user/delete';
    var len = $('#' + id).find('.checkbox_group :checkbox:disabled').length;
    var data = {};
    if (len == 1) {
        data = {
            type: $('#' + id).find('.checkbox_group :checkbox:disabled').attr('name'),
            ugId: id
        };
        deleteData(url, data,pageAddr);
        $('#all_num').html(parseInt($('#all_num').html() - 1));
    } else {
        data = {
            type: $('#' + id).find('.checkbox_group :checkbox:checked').attr('name'),
            ugId: id
        };
        deleteData(url, data,pageAddr);
    }
    //console.log(data);

    $('#' + id).find('.confirm_delete').css('display', 'none');

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
            var select_checkbox = $('.same_table .checkbox_box  :checkbox:checked');
            var ugid_arr = [];
            for (var i = 0; i < select_checkbox.length; i++) {
                var ugid = select_checkbox.eq(i).attr('name');
                ugid_arr.push(ugid);
            }
            var url = host + '/pc/weixin_user/batchDelete';
            var data = {
                openGId: openGID,
                ugIds: ugid_arr
            };
            deleteData(url, data,pageAddr);
            select_checkbox.parents('tr').remove();
            $('#all_num').html(parseInt($('#all_num').html() - ugid_arr.length));
            layer.close(index);
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
//点击编辑修改电话
function edit_number(id) {
    $('#' + id).find('.phone_number_input').removeAttr('readonly');
    $('#' + id).find('.phone_number_input').css('border', '1px solid #D5E5E7');
    $('#' + id).find('.btn_img').css('visibility', 'visible');
}
//点击完成图标，发送参数，保存修改
function edit_number_ok(id) {
    var url = host + "/pc/weixin_user/update";
    var data = {
        phone: $('#' + id).find('.phone_number_input').val(),
        ugId: id
    };
    //console.log(data);
    sendData(url, data);
    $('#' + id).find('.phone_number_input').attr('readonly', 'readonly');
    $('#' + id).find('.phone_number_input').css('border', '1px solid transparent');
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

//点击不同群显示不同内容
function change_group(id, power, peopleCnt) {
    var power = power;
    var openGID = id;
    var data = {
        openGId: openGID,
        key: $('#input_search').val(),
        page: 1,
        type: $('.layui-anim.layui-anim-upbit dd.layui-this').attr('lay-value'),
        perSize: 10
    };
    $('#page_now').html(1);
    $('#all_num').html(peopleCnt);
    var url = host + 'pc/weixin_user/getCnt';
    getNum(id, url);
    var renderType = 0;
    //console.log(data);
    page(data, power,renderType);
}

//下拉框搜索
$('.top_sel .layui-anim,.layui-anim-upbit dd').click(function () {
    select_page(pageAddr);
});
//分页，及搜索
function page(data, power,renderType) {
    //console.log(arguments);
    layui.use('form', function () {
        var form = layui.form;
        $.ajax({
            type: 'POST',
            url: host + '/pc/weixin_user/query',
            data: data,
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
                        var isDisabled = false;
                        var isDirector = res.info.data[i].director ? '班主任' : '';
                        var isTeacher = res.info.data[i].teacher ? '老师' : '';
                        var isPatriarch = res.info.data[i].patriarch ? '学生家长' : '';
                        var identity = '';
                        if (isDirector != '' && isTeacher == '' && isPatriarch == '') {
                            identity += isDirector;
                            isDisabled = true;
                        } else if (isDirector == '' && isTeacher != '' && isPatriarch == '') {
                            identity += (!res.info.data[i].subjects?'':res.info.data[i].subjects) + isTeacher;
                            isDisabled = false;
                        } else if (isDirector == '' && isTeacher == '' && isPatriarch != '') {
                            identity += isPatriarch;
                            isDisabled = false;
                        } else if (isDirector != '' && isTeacher != '' && isPatriarch == '') {
                            identity += isDirector + '|' + (!res.info.data[i].subjects?'':res.info.data[i].subjects) + isTeacher;
                            isDisabled = true;
                        } else if (isDirector != '' && isTeacher == '' && isPatriarch != '') {
                            identity += isDirector + '|' + isPatriarch;
                            isDisabled = true;
                        } else if (isDirector == '' && isTeacher != '' && isPatriarch != '') {
                            identity += (!res.info.data[i].subjects?'':res.info.data[i].subjects) + isTeacher + '|' + isPatriarch;
                            isDisabled = true;
                        } else if (isDirector != '' && isTeacher != '' && isPatriarch != '') {
                            identity += isDirector + '|' + (!res.info.data[i].subjects?'':res.info.data[i].subjects) + isTeacher + '|' + isPatriarch;
                            isDisabled = true;
                        } else {
                            identity += '身份为空';
                        }
                        if (power == 3) {
                            html += '<tr id="' + res.info.data[i].ugId + '" data-patriarch="' + res.info.data[i].patriarch + '">\n                                    <td class="checkbox_box">\n                                        <input type="checkbox" name="' + res.info.data[i].ugId + '" lay-skin="primary"  lay-filter="itemChoose"  title="" ' + (isDisabled ? 'disabled' : '') + '/>\n                                    </td>\n                                    <td>\n                                        <div class="user_img_square">\n                                            <img src="' + res.info.data[i].avatarUrl + '" alt="">\n                                        </div>\n                                        ' + res.info.data[i].aliasName + '\n                                    </td>\n                                    <td>\n                                        ' + identity + '\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].nickName + '</td>\n                                    <td class="hue_gray">\n                                        <input type="text" class="phone_number_input" value="' + (!res.info.data[i].phone?'':res.info.data[i].phone) + '" readonly>\n                                        <a href="javascript:;" class="btn_img" onclick="edit_number_ok(' + res.info.data[i].ugId + ')"></a>\n                                    </td>\n                                    <td>\n                 <div class="tab_operate">                       <a href="javascript:;" class="hue_green" onclick="edit_number(' + res.info.data[i].ugId + ')">\u7F16\u8F91\u624B\u673A\u53F7</a>\n                                        <a href="javascript:;" class="hue_red" ' + (res.info.data[i].director ? 'style="display:none"' : '') + ' onclick="delete_a(' + res.info.data[i].ugId + ')">\u5220\u9664</a>\n                                        <div class="confirm_delete">\n                                            <div>\n                                                ' + (isDisabled ? '该成员在此群助手内有以下两种身份，请选择你要删除的身份' : '是否删除该成员？') + '\n                                            </div>\n                                            <div class="checkbox_group" ' + (isDisabled ? 'style="display:block"' : 'style="display:none"') + '>\n                                                <input type="checkbox" ' + (res.info.data[i].teacher ? 'disabled' : '') + ' name="2" lay-skin="primary" title="\u8001\u5E08"/>\u8001\u5E08\n                                                <input type="checkbox" ' + (res.info.data[i].patriarch ? 'disabled' : '') + ' name="1" lay-skin="primary" title="\u5BB6\u957F"/>\u5BB6\u957F\n                                            </div>\n                                            <div>\n                                                <a href="javascript:;" class="btn_confirm_delete" onclick="del(' + res.info.data[i].ugId + ')">\u786E\u8BA4</a>\n                                                <a href="javascript:;" class="btn_confirm_cancel" onclick="cancel_del(' + res.info.data[i].ugId + ')">\u53D6\u6D88</a> \n                                            </div>\n                                        </div>\n                 </div>                   </td>\n                                </tr>';
                            $('#operate_title').css('display', 'table-cell');
                        } else if (power == 2) {
                            html += '<tr id="' + res.info.data[i].ugId + '">\n                                    <td class="checkbox_box">\n                                                                       </td>\n                                    <td>\n                                        <div class="user_img_square">\n                                            <img src="' + res.info.data[i].avatarUrl + '" alt="">\n                                        </div>\n                                        ' + res.info.data[i].aliasName + '\n                                    </td>\n                                    <td>\n                                        ' + identity + '\n                                    </td>\n                                    <td class="hue_gray">' + res.info.data[i].nickName + '</td>\n                                    <td class="hue_gray">\n                                        <input type="text" class="phone_number_input" value="' + (!res.info.data[i].phone?'':res.info.data[i].phone) + '" readonly>\n                                        <a href="javascript:;" class="btn_img" onclick="edit_number_ok(' + res.info.data[i].ugId + ')"></a>\n                                    </td>\n                                    \n                                </tr>';
                            $('#operate_title').css('display', 'none');
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
                    $('.has_no_data h5').html('该群内还没有该身份的成员！');
                }else if(res.state == 0 && res.info.data.length == 0 && renderType == 3 ) {
                    //搜索框渲染没有数据的样式
                    $('.same_table').css('display', 'none');
                    $('.pagination_box').css('display', 'none');
                    $('.has_no_data').css('display', 'block');
                    $('.has_no_data img').attr('src','images/assets/img_leave_none.png');
                    $('.has_no_data h5').html('没有搜索到该关键字记录，换个关键字试试?');
                }
            },
            dataType: "json",
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

//首页进入头部切换
$.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
};

//# sourceMappingURL=memberManagement-compiled.js.map

//# sourceMappingURL=memberManagement-compiled-compiled.js.map