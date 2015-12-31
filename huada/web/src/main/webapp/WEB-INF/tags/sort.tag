<%@tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="btn-group">
    <a class="btn btn-sm btn-default" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
        排序：${sortTypes[param.sortType]}<i class="fa fa-angle-down"></i>
    </a>

    <div class="dropdown-menu hold-on-click dropdown-checkboxes pull-right">
        <c:forEach items="${sortTypes}" var="entry">
            <li><a href="?sortType=${entry.key}&${searchParams}">${entry.value}</a></li>
        </c:forEach>
    </div>
</div>