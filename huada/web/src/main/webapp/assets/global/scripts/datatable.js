/***
 Wrapper/Helper Class for datagrid based on jQuery Datatable Plugin
 ***/

var Datatable=function(){
    var tableOptions;  // main options
    var dataTable; // datatable object
    var table;    // actual table jquery object
    var tableContainer;    // actual table container object
    var tableWrapper; // actual table wrapper jquery object
    var tableInitialized=false;
    var ajaxParams={}; // set filter mode＝
    var countSelectedRecords=function(){
        var selected=$('tbody > tr > td:nth-child(1) input[type="checkbox"]:checked',table).size();
        var text=tableOptions.dataTable.oLanguage.sGroupActions;
        if(selected>0){
            $('.table-group-actions > span',tableWrapper).text(text.replace("_TOTAL_",selected));
        }else{
            $('.table-group-actions > span',tableWrapper).text("");
        }
    };
    return {
        init:function(options){
            if(!$().dataTable){
                return;
            }
            var the=this;
            options=$.extend(true,{
                src:"",
                filterApplyAction:"filter",
                filterCancelAction:"filter_cancel",
                resetGroupActionInputOnSuccess:true,
                dataTable:{
                    "sDom":"<'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'<'table-group-actions pull-right'>>r><'table-scrollable't><'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'>r>>", // datatable layout
                    "aLengthMenu":[
                        [10,25,50,100,-1],
                        [10,25,50,100,"All"]
                    ],
                    "iDisplayLength":10,
                    "oLanguage":{
                        "sProcessing":'<img src="'+Metronic.getGlobalImgPath()+'loading-spinner-grey.gif"/><span>&nbsp;&nbsp;加载中...</span>',
                        "sLengthMenu":"页<span class='seperator'>,</span>每页显示 _MENU_ 条记录",
                        "sInfo":"(_START_ - _END_),共 _TOTAL_ 条",
                        "sInfoFiltered":"",//"sInfoFiltered": "(从_MAX_记录中过滤)",
                        "sInfoEmpty":"没有找到记录",
                        "sGroupActions":" 选中[_TOTAL_]条记录:",
                        "sAjaxRequestGeneralError":"无法完成请求。请检查你的网络连接",
                        "sEmptyTable":"没有可用的数据",
                        "sZeroRecords":"没有找到匹配的记录",
                        "oPaginate":{"sPrevious":"上页","sNext":"下页","sPage":"","sPageOf":"共"}
                    },
                    "aoColumnDefs":[
                        {'bSortable':false,'aTargets':[ 0 ]}
                    ],
                    "bAutoWidth":false,
                    "bSortCellsTop":true,
                    "sPaginationType":"bootstrap_extended",
                    "bProcessing":true,
                    "bServerSide":true,
                    "sAjaxSource":"",
                    "sServerMethod":"POST",
                    "fnServerData":function(sSource,aoData,fnCallback,oSettings){
                        oSettings.jqXHR=$.ajax({
                            "dataType":'json',
                            "type":"POST",
                            "url":sSource,
                            "data":aoData,
                            "success":function(res,textStatus,jqXHR){
                                if(res.sMessage){
                                    Metronic.alert({type:(res.sStatus=='OK'?'success':'danger'),icon:(res.sStatus=='OK'?'check':'warning'),message:res.sMessage,container:tableWrapper,place:'prepend'});
                                }
                                if(res.sStatus){
                                    if(tableOptions.resetGroupActionInputOnSuccess){
                                        $('.table-group-action-input',tableWrapper).val("");
                                    }
                                }
                                if($('.group-checkable',table).size()===1){
                                    $('.group-checkable',table).attr("checked",false);
                                    $.uniform.update($('.group-checkable',table));
                                }
                                if(tableOptions.onSuccess){
                                    tableOptions.onSuccess.call(undefined,the);
                                }
                                fnCallback(res,textStatus,jqXHR);
                            },
                            "error":function(){
                                if(tableOptions.onError){
                                    tableOptions.onError.call(undefined,the);
                                }
                                Metronic.alert({type:'danger',icon:'warning',message:tableOptions.dataTable.oLanguage.sAjaxRequestGeneralError,container:tableWrapper,place:'prepend'});
                                $('.dataTables_processing',tableWrapper).remove();
                            }
                        });
                    },
                    // pass additional parameter
                    "fnServerParams":function(aoData){
                        //here can be added an external ajax request parameters.
                        $.each(ajaxParams,function(key,value){
                            aoData.push({"name":key,"value":value});
                        });
                    },
                    "fnDrawCallback":function(oSettings){ // run some code on table redraw
                        if(tableInitialized===false){ // check if table has been initialized
                            tableInitialized=true; // set table initialized
                            table.show(); // display table
                        }
                        Metronic.initUniform($('input[type="checkbox"]',table));  // reinitialize uniform checkboxes on each table reload
                        countSelectedRecords(); // reset selected records indicator
                    }
                }
            },options);
            tableOptions=options;
            table=$(options.src);
            tableContainer=table.parents(".table-container");
            $.fn.dataTableExt.oStdClasses.sWrapper=$.fn.dataTableExt.oStdClasses.sWrapper+" dataTables_extended_wrapper";
            dataTable=table.dataTable(options.dataTable);
            tableWrapper=table.parents('.dataTables_wrapper');
            $('.dataTables_length select',tableWrapper).addClass("form-control input-xsmall input-sm");
            if($('.table-actions-wrapper',tableContainer).size()===1){
                $('.table-group-actions',tableWrapper).html($('.table-actions-wrapper',tableContainer).html());
                $('.table-actions-wrapper',tableContainer).remove();
            }
            $('.group-checkable',table).change(function(){
                var set=$('tbody > tr > td:nth-child(1) input[type="checkbox"]',table);
                var checked=$(this).is(":checked");
                $(set).each(function(){
                    $(this).attr("checked",checked);
                });
                $.uniform.update(set);
                countSelectedRecords();
            });
            table.on('change','tbody > tr > td:nth-child(1) input[type="checkbox"]',function(){
                countSelectedRecords();
            });
            table.on('click','.filter-submit',function(e){
                e.preventDefault();
                the.setAjaxParam("sAction",tableOptions.filterApplyAction);
                $('textarea.form-filter, select.form-filter, input.form-filter:not([type="radio"],[type="checkbox"])',table).each(function(){
                    the.setAjaxParam($(this).attr("name"),$(this).val());
                });
                $('input.form-filter[type="checkbox"]:checked, input.form-filter[type="radio"]:checked',table).each(function(){
                    the.setAjaxParam($(this).attr("name"),$(this).val());
                });
                dataTable.fnDraw();
            });
            table.on('click','.filter-cancel',function(e){
                e.preventDefault();
                the.setAjaxParam("sAction",tableOptions.filterCancelAction);
                $('textarea.form-filter, select.form-filter, input.form-filter',table).each(function(){
                   $(this).val("");
                    the.setAjaxParam($(this).attr("name"),'');
                });
                $('input.form-filter[type="checkbox"]',table).each(function(){
                    $(this).attr("checked",false);
                });
//                the.clearAjaxParams();
                dataTable.fnDraw();
            });
        },
        getSelectedRowsCount:function(){
            return $('tbody > tr > td:nth-child(1) input[type="checkbox"]:checked',table).size();
        },
        getSelectedRows:function(){
            var rows=[];
            $('tbody > tr > td:nth-child(1) input[type="checkbox"]:checked',table).each(function(){
                rows.push({name:$(this).attr("name"),value:$(this).val()});
            });
            return rows;
        },
        addAjaxParam:function(name,value){
            ajaxParams[name]=value;
        },
        setAjaxParam:function(name,value){
            ajaxParams[name]=value;
        },
        clearAjaxParams:function(name,value){
            ajaxParams=[];
        },
        getDataTable:function(){
            return dataTable;
        },
        getTableWrapper:function(){
            return tableWrapper;
        },
        gettableContainer:function(){
            return tableContainer;
        },
        getTable:function(){
            return table;
        }

    };
};