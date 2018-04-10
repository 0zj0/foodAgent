"use strict";

$(function () {
	clearAllCookie();
	$.post(host+"pc/sign", function (data) {
		$("#qrcode").attr("src", "pc/login/qrcode");
	}, 'json');
});

function getCookie(name) {
    var arr,
        reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    console.log(document.cookie);
    console.log(document.cookie.match(reg));
    if (arr = document.cookie.match(reg)) return unescape(arr[2]);else return null;
}

function clearAllCookie() {  
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);  
    if(keys) {  
        for(var i = keys.length; i--;)  
            document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString();
    }  
} 

//# sourceMappingURL=login-compiled.js.map