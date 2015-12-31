/**
 * Created by xushuangyong on 14-8-25.
 * 选择员工空间依赖jstree jquery datatable
 */
var SelectEmpCmp = function (modalId, orgId, posId, tableId, resultId, multiple, callback) {
    //当multiple
    this.multiple = multiple;
    //回调函数
    if (!modalId) {
        console.error("modalId 为必须属性")
        return;
    }
    if (!orgId) {
        console.error("orgId 为必须属性")
        return;
    }

    if (!posId) {
        console.error("posId 为必须属性")
        return;
    }

    if (!tableId) {
        console.error("tableId 为必须属性")
        return;
    }

    if (!resultId) {
        console.error("resultId 为必须属性")
        return;
    }

    if (!callback) {
        console.error("回调函数callback 必须输入")
        return;
    }

    this.callback = callback;
    this.selectPosId = undefined;
    this.selectOrgId = undefined;
    var that = this;
    this.orgTree = function (orgId) {
        var orgTree = $("#" + orgId).jstree({
            "core": {
                "animation": 0,
                "themes": {
                    theme: "classic",
                    "dots": true,
                    "icons": true
                },
                // so that create works
                "check_callback": true,
                'data': {
                    'url': function (node) {
                        return ctx + '/system/organization/findOrganzationNodes';
                    },
                    'data': function (node) {
                        return { 'parent': node.id };
                    },
                    async: false
                }
            },
            "types": {
                "default": {
                    "valid_children": ["default", "file"]
                },
                "file": {
                    "icon": "glyphicon glyphicon-file",
                    "valid_children": []
                }
            },
            //"state" : { "key" : "demo3" },
            "plugins": [  "state", "types", "wholerow"]

        });

        orgTree.bind("select_node.jstree", function (node, selectd, event) {
            var node = selectd.node;
            var origObj = node.original;
            var inst = selectd.instance;
            that.selectOrgId = origObj.id;
            that.empTable.fnReloadAjax();
        });
    }
    this.posTree = function (posId) {
        var orgTree = $("#" + posId).jstree({
            "core": {
                "animation": 0,
                "themes": {
                    theme: "classic",
                    "dots": true,
                    "icons": true
                },
                // so that create works
                "check_callback": true,
                'data': {
                    'url': function (node) {
                        return ctx + '/system/position/findPositionNodes';
                    },
                    'data': function (node) {
                        return { 'parent': node.id };
                    },
                    async: false
                }
            },
            "types": {
                "default": {
                    "valid_children": ["default", "file"]
                },
                "file": {
                    "icon": "glyphicon glyphicon-file",
                    "valid_children": []
                }
            },
            //"state" : { "key" : "demo3" },
            "plugins": [  "state", "types", "wholerow"]

        });
        orgTree.bind("select_node.jstree", function (node, selectd, event) {
            var node = selectd.node;
            //  alert(orgTree.is_checked(node));
            var origObj = node.original;
            var inst = selectd.instance;
            that.selectPosId = origObj.id;
            that.empTable.fnReloadAjax();
            return false;
        });
    }
    this.addEmpTable = function (tableId) {
        this.empTable = $('#' + tableId).dataTable({
            "bProcessing": true,//查询显示
            "bStateSave": false,
            "bServerSide": true,//从服务器加载
            "bAutoWidth": true,//列自适应浏览器宽度
            "bDestroy": true,
            "bRetrieve": true,
            "bSort": true,//排序
            "bPaginate": true,//是否开启滚动条
            "sServerMethod": "POST",
            "oLanguage": {
                "sProcessing": "处理中...",
                "sLengthMenu": "每页显示 _MENU_ 条记录",
                "sZeroRecords": "抱歉, 没有找到记录",
                "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
                "sInfoEmpty": "",
                "sInfoFiltered": "(从 _MAX_ 条数据中检索)", "sSearch": "搜索"
            },
            "aLengthMenu": [
                [10, 25, 50, 1000],
                [10, 25, 50, "All"]
            ],
            "aoColumnDefs": [
                { "bSortable": false, "aTargets": [ 0 ] }
            ],//指定某列不可排序
            "sAjaxSource": ctx + "/system/user/listSelectEmp?r=" + Math.random(),
            "fnServerData": function (sSource, aoData, fnCallback) {
                if (that.selectPosId == 0) {
                    that.selectPosId = undefined;

                }
                if (that.selectOrgId == 0) {
                    that.selectOrgId = undefined;
                }
                aoData.push({"name": "orgId", "value": that.selectOrgId});
                aoData.push({"name": "posId", "value": that.selectPosId});
                $.ajax({
                    "dataType": 'json',
                    "type": "POST",
                    "url": sSource,
                    "data": aoData,
                    "success": function (resp) {
                        fnCallback(resp);//回填表格数据
                    }
                });
            },
            "aoColumns": [
                { "sWidth": "1%", mData: "id", "sTitle": '<input type="checkbox" class= "checkAllBox" onclick="checkAllBox(this)"  title="全选" class="group-checkable" >', "mRender": function (data, type, full) {
                    return '<div class="checker" ><span class=""><input type="checkbox" class="checkboxes" checked="" name="checkBox" value="' + full.id + '"></span></div>';
                }},
                { "sTitle": "登陆名", "mData": "loginName", "mRender": function (data, type, full) {
                    return displayEmpLink(data, full.id);
                }
                },
                { "sTitle": "姓名", "mData": "name"},
                /*     { "sTitle": "组织", "mData": "orgs", "sWidth": "200px"},
                 { "sTitle": "岗位", "mData": "poss", "sWidth": "200px"},*/
                { "sTitle": "状态", "mData": "status", "sDefaultContent": "", "mRender": function (data, type, full) {
                    return getStatusDisplay(data);
                }}
            ]
        });
        jQuery('#' + tableId + '_wrapper .dataTables_filter input').addClass("form-control input-medium input-inline"); // modify table search input
        jQuery('#' + tableId + '_wrapper .dataTables_length select').addClass("form-control input-small"); // modify table per page dropdown
        jQuery('#' + tableId + '_wrapper .dataTables_length select').select2({
            showSearchInput: false
        });
        jQuery('#' + tableId).on('change', 'tbody tr .checkboxes', function () {
            if ($(this).parents('span').hasClass("checked") == false) {
                $("#" + tableId + " tbody tr .checkboxes").parents("span").removeClass("checked");
            }
            $(this).parents('span').toggleClass("checked");
        });

        var jqObject = $("div#" + modalId + " div." + resultId);

        $('#' + tableId + ' tbody tr').live('dblclick', function () {
            var nRow = that.empTable.fnGetData(this);
            var flag = true;
            //防止插入重复数据
            var loginName = nRow.loginName;
            var eleHtml = '<a class="delete btn default btn-xs black" href="javascript:;"><i class="fa fa-trash-o"></i>' + loginName + '</a>';
            var ele = $(eleHtml);
            ele.data("data", { id: nRow.id, loginName: nRow.loginName, name: nRow.name})
            if (!that.multiple) {
                jqObject.html("");
            }
            $("div#" + modalId + " div." + resultId + " a.delete ").each(function () {
                var sData = $(this).data("data");
                if (sData.id == nRow.id) {
                    flag = false;
                    return true;
                }
            });
            if (flag) {

                jqObject.append(ele);
            }
        });

        $("div#" + modalId + " div." + resultId + ' a.delete').live('click', function (e) {
            e.preventDefault();
            $(this).remove();
        });
        $("#" + tableId).closest("div.modal").find("button.btn.btn-primary").bind("click", function () {
            $(this).closest("div.modal").modal('hide');
            var selectData = [];
            $("div#" + modalId + " div." + resultId + " a.delete ").each(function () {
                var sData = $(this).data("data");
                selectData.push(sData);
            });
            //回调函数
            that.callback(selectData)
        });

    }
    //清空选择值函数
    this.cleanData = function () {
        $("div#" + modalId + " div." + resultId + " a.delete ").each(function () {
            $(this).remove();
        });
    }

    this.show = function () {
        $("div#" + modalId).modal('show');
    }

    this.hide = function () {
        $("div#" + modalId).modal('hide');
    }


    $("#" + modalId + " div.tree-1 div.btn-group a.cleanSelect").bind("click", function () {
        var inst = $.jstree.reference(orgId);
        var array = inst.get_selected();
        inst.deselect_node(array);
        that.selectPosId = undefined;

    })

    $("#" + modalId + " div.tree-1 div.btn-group a.refreshTree").bind("click", function () {
        var inst = $.jstree.reference(orgId);
        inst.refresh();
    })


    $("#" + modalId + " div.tree-2 div.btn-group a.cleanSelect").bind("click", function () {
        var inst = $.jstree.reference(posId);
        var array = inst.get_selected();
        inst.deselect_node(array);
        that.selectPosId = undefined;

    })

    $("#" + modalId + " div.tree-2 div.btn-group a.refreshTree").bind("click", function () {
        var inst = $.jstree.reference(posId);
        inst.refresh();
    })

    $("#" + modalId + " div.result-col div.btn-group a.cleanSelect").bind("click", function () {
        $("div#" + modalId + " div." + resultId).html("");
    })

    this.orgTree(orgId);
    this.posTree(posId);
    this.addEmpTable(tableId);
}

function displayEmpLink(loginName, id) {
    var appURL = ctx;
    var url = appURL + "/system/employee/detail?id=" + id;
    return '<a target="_blank" href="' + url + '" >' + loginName + '<a>';
}
