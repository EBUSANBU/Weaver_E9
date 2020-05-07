
/**
 * 浏览按钮只读。
 */
var fieldReadOnly_broswer = function(fieldId) {
    jQuery('#' + fieldId + '_browserbtn').hide();
    jQuery('#innerContent' + fieldId + 'div').css('cursor', 'default');
    jQuery('#innerContent' + fieldId + 'div').css('border', '0px');
    jQuery('#innerContent' + fieldId + 'div').css('margin-right', '0px');
    jQuery('#out' + fieldId + 'div').css('border', '0px');
    jQuery('#out' + fieldId + 'div').css('margin-right', '-12px');
    jQuery('#inner' + fieldId + 'div').css('margin-left', '0px');
    jQuery('#out' + fieldId + 'div').css('margin-right', '-12px');
    jQuery('#' + fieldId + '__').attr('readonly', 'readonly');
    jQuery('#' + fieldId + 'span>.e8_showNameClass>.e8_delClass').remove();
};

/**
 * 设置浏览按钮的值。
 * @param fieldId
 * @param value
 * @param text
 */
var setFieldValue_browser = function(fieldId, value, text) {
    jQuery('#' + fieldId).val(value || '');
    jQuery('#' + fieldId + 'span').html(text || '');
    jQuery('#' + fieldId + 'spanimg').html('');
    jQuery('#' + fieldId + 'span').attr('style', 'color: #123885 !important');
};

/**
 * 设置文本框的值。
 * @param fieldId
 * @param value
 * @param text
 */
var setFieldValue_text = function(fieldId, value,text) {
    jQuery('#' + fieldId).val(value || '');
    jQuery('#' + fieldId + 'span').html(text || '');
};

/**
 * 给字段添加必填验证。
 */
var addInputCheckField = function(fieldId, spanImgId) {
    jQuery('#' + fieldId).attr('viewtype', '1');
    var fieldStr = jQuery('input[name=needcheck]').val();
    if (fieldStr.charAt(fieldStr.length - 1) != ',') {
        fieldStr += ',';
    }
    jQuery('input[name=needcheck]').val(fieldStr + fieldId + ',');
    if(jQuery('#' + fieldId).val().replace(/(^s*)|(s*jQuery)/g, "").length ===0)
        jQuery('#' + spanImgId).html('<img src="/images/BacoError_wev8.gif" align="absMiddle">');
};

/**
 * 移除字段必填验证。//viewtype 0 编辑 1必填
 */
var removeInputCheckField = function(fieldId, spanImgId) {
    jQuery('#' + fieldId).attr('viewtype', '0');
    var fieldStr = jQuery('input[name=needcheck]').val();
    jQuery('input[name=needcheck]').val(fieldStr.replace(fieldId + ',', ''));
    jQuery('#' + spanImgId).html('');
};
/**
 * 隐藏字段。 只能隐藏下拉框，文本框，多行文本框，且不改变该字段的属性，
 * 要改变结合上面的方法一起实现
 */
var hideInputField = function(fieldId) {
    jQuery('#' + fieldId).hide();
};
/**
 * 根据id隐藏tr
 */
var hideTRByField = function(fieldId) {
    $('#'+fieldId+'_tdwrap').parent().hide();
};
/**
 * 根据id隐藏tr
 */
var showTRByField = function(fieldId) {
    $('#'+fieldId+'_tdwrap').parent().show();
};

/**
 * 显示字段。只能显示下拉框，文本框，多行文本框，且不改变该字段的属性，
 * 要改变结合上面的方法一起实现
 */
var showInputField = function(fieldId) {
    jQuery('#' + fieldId).show();
};
/**
 * 隐藏字段。 只能隐藏下拉框，文本框，多行文本框，且不改变该字段的属性，
 * 要改变结合上面的方法一起实现
 */
var hideInputField = function(id,name) {
    jQuery('#' + id).css('visibility','hidden');
    jQuery('#' + id).css('border-bottom-color','#ffffff');
    jQuery('#' + id).css('border-right-color','#ffffff');
    jQuery('#' + id).css('border-left-color','#ffffff');
    jQuery('#' + id).css('border-top-color','#ffffff');
    jQuery('[name='+name+']').css('visibility','hidden');
};

/**
 * 显示字段。只能显示下拉框，文本框，多行文本框，且不改变该字段的属性，
 * 要改变结合上面的方法一起实现
 */
var showInputField = function(id,name) {
    jQuery('#' + id).css('visibility','visible');
    jQuery('#' + id).css('border-bottom-color','#d9d9d9');
    jQuery('#' + id).css('border-right-color','#d9d9d9');
    jQuery('#' + id).css('border-left-color','#d9d9d9');
    jQuery('#' + id).css('border-top-color','#d9d9d9');
    jQuery('[name='+name+']').css('visibility','visible');
};

//隐藏域通用监听方法
function HiddenListener(elementId,fn) {
    var oldValue = jQuery("#" + elementId).val();
    //每隔500毫秒 调用一次函数或计算表达式
    var obj = setInterval(checkValue,500);
    function checkValue(){
        var newValue = jQuery("#" + elementId).val();
        if(newValue != oldValue){
            fn(oldValue,newValue);
            oldValue = newValue;
        }
    }
    //停止调用
    function removeHiddenListener(){
        clearInterval(obj);
    }
}

//*****提交方式一**************************************************
/**
 * 重写提交事件
 * @param onSubmit  提交前执行方法，执行成功放回true，允许提交
 * @param needSave  保存事件是否需要执行onSubmit， true为需要
 * @param failed  提交前执行方法 执行失败调用的方法，即当onSubmit执行失败调用此方法
 */
var overwriteSubmit = function(onSubmit,needSave,failed) {
    var doSubmitBackOld = doSubmitBack, doSubmitNoBackOld = doSubmitNoBack, doSave_nNewOld = doSave_nNew;

    doSubmitBack = function(obj) {
        if (onSubmit()) {
            doSubmitBackOld(obj);
        } else {
            if ( failed !== null && typeof failed === "function") failed();
        }
    };

    doSubmitNoBack = function(obj) {
        if (onSubmit()) {
            doSubmitNoBackOld(obj);
        } else {
            if ( failed !== null && typeof failed === "function") failed();
        }
    };
    doSave_nNew = function(obj) {
        if (needSave !== null && needSave) {
            if (onSubmit()) {
                doSave_nNewOld(obj);
            } else {
                if ( failed !== null && typeof failed === "function") failed();
            }
        } else {
            doSave_nNewOld(obj);
        }

    };
};

/**
 * 获取url的参数
 * @param key
 * @returns {any}
 */
function getQueryString(key){
    var reg = new RegExp("(^|&)"+key+"=([^&]*)(&|$)");
    var result = window.location.search.substr(1).match(reg);
    return result?decodeURIComponent(result[2]):null;
}

/**
 * 从流程表单获取wid
 */

function getWid() {
    return jQuery('input[name=workflowid]').val();
}


//获取表单字段
function getModileFields(workflowid){
    var fieldDatas;
    jQuery.ajax({
        type:"POST",
        url: "/mobile/plugin/weaversj/x/workflow/publicUtil/getfieldid.jsp",
        data: 'wid='+workflowid,
        dataType:"json",
        async: false,
        success: function(data){
            fieldDatas = data;
        }
    });
    return fieldDatas;
}

//获取表单字段
function getFields(workflowid){
    var fieldDatas;
    jQuery.ajax({
        type:"POST",
        url: "/weaversj/workflow/x/publicUtil/getfieldid.jsp",
        data: 'wid='+workflowid,
        dataType:"json",
        async: false,
        success: function(data){
            fieldDatas = data;
        }
    });
    return fieldDatas;
}

//获取建模字段
function getFieldsToMode(formid){
    var fieldDatas;
    jQuery.ajax({
        type:"POST",
        url: "/weaversj/workflow/x/publicUtil/getfieldidToMode.jsp",
        data: 'wid='+formid,
        dataType:"json",
        async: false,
        success: function(data){
            fieldDatas = data;
        }
    });
    return fieldDatas;
}

//获取当前用户系统语言
function getUserLanguage(){
    var userLanguage;
    jQuery.ajax({
        type:"POST",
        url: "/mobile/plugin/weaversj/x/workflow/publicUtil/getUserLanguage.jsp",
        contentType:'application/x-www-form-urlencoded',
        dataType:"text",
        async: false,
        success: function(data){
            userLanguage = data.trim();
        }
    });
    return userLanguage;
}

/*

jQuery(overwriteSubmit(function(){
    // 验证成功返回true
    // 验证失败返回false
    return false;
},false,function () {
    //验证失败执行方法
    alert("ceshi");
}));

*/

//*****提交方式二**************************************************
/*

var fieldIdOfCNY = "field13777";
var fieldIdOfKXJEHZ = "field15728";
jQuery(function () {
    checkCustomize = function (){
        var v1 = parseFloat(jQuery('#' + fieldIdOfCNY ).val());
        var v2 = parseFloat(jQuery('#' + fieldIdOfKXJEHZ ).val());
        console.log(v1+':' + v2);
        if(v1 < v2){
            alert('总集分包合同金额应不小于款项金额');
            return false;
        }
        return true;
    }
});*/
