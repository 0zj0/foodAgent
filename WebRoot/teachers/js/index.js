'use strict';

/*首页tab切换样式*/


$('.new_notes a').click(function () {
    var oDate = new Date(); //实例一个时间对象；
    var year = oDate.getFullYear();   //获取系统的年；
    var mon = oDate.getMonth()+1;   //获取系统月份，由于月份是<a href="https://www.baidu.com/s?wd=%E4%BB%8E0%E5%BC%80%E5%A7%8B&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1dWnHTvrAPWnvcvn19bnv7b0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3EnHRsrH6krHcsnWbLrjf1PWb3n0" target="_blank" class="baidu-highlight">从0开始</a>计算，所以要加1
    var day = oDate.getDate(); // 获取系统日，
    var hour = oDate.getHours(); //获取系统时，
    var min = oDate.getMinutes(); //分
    var sec = oDate.getSeconds(); //秒
    $('#readTime').html(year + '/' + mon + '/' +day + ' ' +hour+':'+min+':'+sec);
    $('#now_time').html(year + '/' + mon + '/' +day);
    $('#new_notes').css('display', 'block');
    $('.home_page').css('display', 'none');
});
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
            if (res.state == 0 && res.info.length > 0) {
                for (var i in res.info) {
                    if(res.info[i].newLeave > 0){
                        $('.new_msg_mark').css('display',"inline-block");
                        break;
                    }else{
                        $('.new_msg_mark').css('display',"none");
                    }
                }
            }
        },
        error:function(){

        }
    });
    var data = {
        page: 1,
        perSize: 10
    };
    page(data);
});
//分页查询下一页
function next_page() {
    var num_add = $('#page_now').text();
    var data = {
        page: parseInt(num_add) + 1,
        perSize: 10
    };
    page(data);
    $('#page_now').html(parseInt(num_add) + 1);
}
//分页查询上一页
function prev_page() {
    var num_add = $('#page_now').text();
    var data = {
        page: parseInt(num_add) - 1,
        perSize: 10
    };
    //console.log(data);
    page(data);
    $('#page_now').html(parseInt(num_add) - 1);
}
//分页跳转
function jump_page() {
    var data = {
        page: $('#page_input').val(),
        perSize: 10
    };
    page(data);
}
//分页
function page(data) {
    //群列表数据渲染
    $.ajax({
        url: host+"pc/weixin_group/query/page",
        data: data,
        type: 'post',
        cache: false,
        dataType: 'json',
        success: function success(res) {
            var html = '';
            if (res.state == 0 && res.info.data.length > 0) {
                $('.empty_null').css('display', 'none');
                $('.main-content').css('display', 'block');
                for (var i in res.info.data) {
                    html += '<li class=\'group_item\'>\n                            <div class=\'class_info_box\'>\n                                <div class=\'class_img\'>\n                                    <img src=\'' + res.info.data[i].groupPic + '\' alt=\'\'>\n                                </div>\n                                <div class=\'class_detail\'>\n                                    <p class=\'class_name\'>' + res.info.data[i].groupName + '</p>\n                                    <a href=\'javascript:;\'>' + res.info.data[i].identity + '</a>\n                                    <div class=\'class_item\'>\n                                        <ul>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'homework.html?openGId=' + res.info.data[i].openGId + '\'>\u5DF2\u5E03\u7F6E\u4F5C\u4E1A<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].totalWork + '</p>\n                                            </li>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'classNotes.html?openGId=' + res.info.data[i].openGId + '\'>\u5DF2\u53D1\u5E03\u901A\u77E5<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].totalNotify + '</p>\n                                            </li>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'transcript.html?openGId=' + res.info.data[i].openGId + '\'>\u6210\u7EE9\u5355<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].totalAchievement + '</p>\n                                            </li>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'leave.html?openGId=' + res.info.data[i].openGId + '\'>\u8BF7\u5047\u5BA1\u6279<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].totalLeave + '</p>\n                                            </li>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'shareFile.html??openGId=' + res.info.data[i].openGId + '\'>\u5171\u4EAB\u6587\u4EF6<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].totalShare + '</p>\n                                            </li>\n                                            <li class=\'class_detail_item\'>\n                                                <p><a href=\'memberManagement.html?openGId=' + res.info.data[i].openGId + '\'>\u73ED\u7EA7\u6210\u5458<i class="layui-icon">&#xe602;</i></a></p>\n                                                <p>' + res.info.data[i].peopleCnt + '</p>\n                                            </li>\n                                        </ul>\n                                    </div>\n                                </div>\n                            </div>\n                        </li>';
                }
                $('#page_all').html(res.info.totalPage);
                $('#group_page_num>span').text(parseInt(data.page));
                $('.group_list').html(html);
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
            } else {
                $('.has_data').css('display', 'none');
                $('.empty_null').css('display', 'block');
            }
        },
        error: function error(data) {
            $('.has_data').css('display', 'none');
            $('.errorPage').css('display', 'block');
        }
    });
}

//# sourceMappingURL=index-compiled.js.map

//# sourceMappingURL=index-compiled-compiled.js.map