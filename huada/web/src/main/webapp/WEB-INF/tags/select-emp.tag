<%@tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" type="java.lang.String" required="true" %>
<%@ attribute name="modalId" type="java.lang.String" required="true" %>
<%@ attribute name="orgId" type="java.lang.String" required="true" %>
<%@ attribute name="posId" type="java.lang.String" required="true" %>
<%@ attribute name="tableId" type="java.lang.String" required="true" %>
<%@ attribute name="resultId" type="java.lang.String" required="true" %>
<div class="modal fade" id="${modalId}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" style="width:1250px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">${title}</h4>
            </div>
            <div class="modal-body" style="margin:5px;">
                <div class="emp-select-box">
                    <div class="trees-col">
                        <div class="-select-box">

                            <div class="tree-1" style="height:255px;">
                                <div class="actions"
                                     class="green-haze">
                                    <div class="btn-group">
                                        <a href="#" class="btn btn-xs red cleanSelect">
                                            清除
                                        </a>
                                    </div>
                                    <div class="btn-group">
                                        <a href="#" class="btn btn-xs green refreshTree">
                                            刷新
                                        </a>
                                    </div>
                                </div>
                                <div id="${orgId}"
                                     style="margin-top:5px;margin-bottom:2px;max-height:212px;overflow:auto;"></div>
                            </div>
                            <div class="tree-2" style="height:255px;">
                                <div class="actions"
                                     class="green-haze">
                                    <div class="btn-group">
                                        <a href="#" class="btn btn-xs red cleanSelect">
                                            清除
                                        </a>
                                    </div>
                                    <div class="btn-group">
                                        <a href="#" class="btn btn-xs green refreshTree">
                                            刷新
                                        </a>
                                    </div>
                                </div>
                                <div id="${posId}" style="max-height:212px;overflow:auto;"></div>
                            </div>
                        </div>
                    </div>
                    <div class="emp-col">
                        <div class="row" id="tableDatas" style="padding:2px;">
                            <div class="col-md-12">
                                <div class="portlet box green-haze">
                                    <div class="portlet-title">
                                        <div class="caption">
                                            <i class="fa fa-编辑"></i>员工
                                        </div>
                                    </div>
                                    <div class="portlet-body">
                                        <table class="table table-striped table-hover table-bordered"
                                               id="${tableId}">
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="result-col">
                        <div class="actions"
                             class="green-haze">
                            <div class="btn-group">
                                <a href="#" class="btn btn-xs red cleanSelect">
                                    清空
                                </a>
                            </div>
                        </div>
                        <div class="${resultId}" style="padding-top:5px;height:390px;overflow:auto;"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" data-dismiss="modal" class="btn btn-default">取消</button>
                    <button type="button" class="btn btn-primary">保存</button>
                </div>
            </div>
        </div>
    </div>
</div>