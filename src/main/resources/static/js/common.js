var host="https://luxinx.com/account";
//设置下拉选择功能
//objId为input框id data为数组类型的下拉选择数据，func为回调函数
var dataselect = function(objId,data,func){
    //账户
    var userPicker = new mui.PopPicker();
    userPicker.setData(data);

    var pickerInput = document.getElementById(objId);
    pickerInput.addEventListener('tap', function(event) {
        userPicker.show(function(items) {
            var choose=items[0];
            pickerInput.value = choose.text;
            var dataoption = pickerInput.setAttribute("data-options",choose.value);
            if(func){
                func();
            }
            //返回 false 可以阻止选择框的关闭
            //return false;
        });
    }, false);
}


//获取当前日期 yyyy-mm-dd
var getcurrentdate = function(){
    var date = new Date();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentDate = date.getFullYear() + "-" + month + "-" + strDate;
    return currentDate;
}


/**
 * js转换long型时间
 * new Date(long型时间戳).Format("yyyy-MM-dd hh:mm")
 * new Date(long型时间戳).Format("yyyy-MM-dd hh:mm:ss")
 * **/
Date.prototype.Format = function(format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format)) format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}

var number_format = function (number, decimals, dec_point, thousands_sep) {
    /*
    * 参数说明：
    * number：要格式化的数字
    * decimals：保留几位小数
    * dec_point：小数点符号
    * thousands_sep：千分位符号
    *  var num=number_format(1234567.089, 2, ".", ",");//1,234,567.09

    * */
    number = (number + '').replace(/[^0-9+-Ee.]/g, '');
    var n = !isFinite(+number) ? 0 : +number,
        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
        sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
        dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
        s = '',
        toFixedFix = function (n, prec) {
            var k = Math.pow(10, prec);
            return '' + Math.ceil(n * k) / k;
        };

    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
    var re = /(-?\d+)(\d{3})/;
    while (re.test(s[0])) {
        s[0] = s[0].replace(re, "$1" + sep + "$2");
    }

    if ((s[1] || '').length < prec) {
        s[1] = s[1] || '';
        s[1] += new Array(prec - s[1].length + 1).join('0');
    }
    return s.join(dec);
}