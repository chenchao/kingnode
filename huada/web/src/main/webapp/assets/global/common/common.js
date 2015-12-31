/**
 * Created by xushuangyong on 14-7-24.
 */
/**清空页面缓存已经错误提示信息
 * @param form
 */
function clearForm(form){
    if(form){
        form.get(0).reset();
        var form_error=$('.form-group',form);
        if(form_error){
            form_error.removeClass('has-error');
        }
        $('.alert-danger',form).hide();
        $('.alert-success',form).hide();
        $("span.help-block",form).html('');
    }
}
function getSelected(table){
    var selected=[];
    table.find("tbody tr").each(function(){
        var nRow=table.fnGetData(this);
        if(nRow){
            if($(this).find("input.checkboxes").parents("span").hasClass("checked")){
                selected.push(nRow.id)
            }
        }
    });
    return selected;
}
function getSelectedRows(table){
    var selected=[];
    table.find("tbody tr").each(function(){
        var nRow=table.fnGetData(this);
        if(nRow){
            if($(this).find("input.checkboxes").parents("span").hasClass("checked")){
                selected.push(nRow)
            }
        }
    });
    return selected;
}
/**
 * 选择所有checkbox
 * @param th
 * @param name
 */
function checkAllBox(th){
    if(th){
        var table=$(th).closest("table");
        if(th.checked){
            table.find("tr input.checkboxes").attr("checked",true).parent('span').addClass('checked');
        }else{
            table.find("tr input[name='checkBox'].checkboxes").attr("checked",false).parent('span').removeClass('checked');
        }
    }
}
//异步刷新表格
function reloadTable(id,serVal){
    var oTable;
    if(id){
        oTable=$('#'+id).dataTable();
    }
    if(oTable){
        var oSettings=oTable.fnSettings();
        oSettings.aoServerParams=[];
        if(serVal){
            oSettings.aoServerParams.push({
                "fn":function(aoData){
                    aoData.push({"name":"key_1","value":serVal});
                }});
        }else{
            var name=$('#appUl').val();
            if(name=='-1'){
                name='';
            }
            oSettings.aoServerParams.push({
                "fn":function(aoData){
                    aoData.push({"name":"key_1","value":{"appId":name}});
                }});
        }
        oTable.fnReloadAjax();
    }
}
// 答应对象
function alertObj(obj){
    if(obj){
        var de="";
        for(var i in obj){
            de+=i+"="+obj[i]+"\n";
        }
        alert(de);
    }
    alert("");
}
function getStatusDisplay(status){
    if("enabled"==status){
        return"启用";
    }
    return"未启用";
}
// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format=function(fmt){ //author: meizz
    var o={
        "M+":this.getMonth()+1,                 //月份
        "d+":this.getDate(),                    //日
        "h+":this.getHours(),                   //小时
        "m+":this.getMinutes(),                 //分
        "s+":this.getSeconds(),                 //秒
        "q+":Math.floor((this.getMonth()+3)/3), //季度
        "S":this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+k+")").test(fmt))
            fmt=fmt.replace(RegExp.$1,(RegExp.$1.length==1)?(o[k]):(("00"+o[k]).substr((""+o[k]).length)));
    return fmt;
};
function displayAutoInfo(data){
    if(data){
        var name=data.name||" ";
        var transmission=data.transmission||" ";
        if(transmission=="MANUAL"){
            transmission="手动";
        }
        if(transmission=="MIX"){
            transmission="混合";
        }
        if(transmission=="AUTO"){
            transmission="自动";
        }
        var seating=data.seating||" ";
        var info=name+","+transmission+","+seating+"<nobr>";
        return info;
    }
    return";"
}
function displayTime(time){
    if(time&&isNaN(time)==false){
        var newTime=new Date(time).Format("yyyy-MM-dd hh:mm");
        return newTime;
    }
    return"";
}
function displayDriver(driver){
    return"<div>"+(driver.name||"")+"</div>"+"<div>"+(driver.mobile||"")+"</div>";
}
function clearForm(formObj){
    var formEl=formObj.elements;
    for(var i=0; i<formEl.length; i++){
        var element=formEl[i];
        if(element.type=='submit'){
            continue;
        }
        if(element.type=='reset'){
            continue;
        }
        if(element.type=='button'){
            continue;
        }
        if(element.type=='hidden'){
            continue;
        }
        if(element.type=='text'){
            element.value='';
        }
        if(element.type=='textarea'){
            element.value='';
        }
        if(element.type=='checkbox'){
            element.checked=false;
        }
        if(element.type=='radio'){
            element.checked=false;
        }
        if(element.type=='select-multiple'){
            element.selectedIndex= -1;
        }
        if(element.type=='select-one'){
            element.selectedIndex= -1;
        }
    }
}
//显示订单的状态
function dispalyState(data){
    //  APPLY,APPROVAL,REJECT,DESELECT,LEND,RESTORE,OVERTIME,TIMEOUT
    //    RESERVE,LEND,RESTORE,SCRAPPING
    if(data=="APPLY"){
        return"申请"
    }
    if(data=="APPROVAL"){
        return"审批通过"
    }
    if(data=="REJECT"){
        return"驳回"
    }
    if(data=="DESELECT"){
        return"取消"
    }
    if(data=="LEND"){
        return"借出"
    }
    if(data=="RESTORE"){
        return"库存"
    }
    if(data=="OVERTIME"){
        return"超时"
    }
}
function displayUserTime(full){
    try{
        var lendDate=full.lendDate;
        var restoreDate=full.restoreDate;
        var newTime1=new Date(lendDate).Format("yyyy-MM-dd hh:mm");
        var newTime2=new Date(restoreDate).Format("yyyy-MM-dd hh:mm");
        return newTime1+" 至 "+newTime2;
    }catch(e){
        console.log(e);
    }
}

/**
 * 全选 按钮
 * @param th
 * @param name
 */
function checkAllBox(th,name){
    if(th){
        if(th.checked){
            if(name){
                $("input[name='"+name+"']").attr("checked",true).parent('span').addClass('checked');
                $(th).parent("span").addClass("checked");
            }else{
                $("input[name='checkBox']").attr("checked",true).parent('span').addClass('checked');
            }
        }else{
            if(name){
                $(th).parent("span").removeClass("checked");
                $("input[name='"+name+"']").attr("checked",false).parent('span').removeClass('checked');
            }else{
                $("input[name='checkBox']").attr("checked",false).parent('span').removeClass('checked');
            }
        }
    }
}
//操作成功后的提示框，两秒钟后自动关闭
function alertHint(msg,callback){
    if(!msg){
        msg="操作成功.";
    }
    bootbox.alert(msg);
    setTimeout(function(){
        bootbox.hideAll();
        if(callback){
            callback();
        }
    },2000);
}

