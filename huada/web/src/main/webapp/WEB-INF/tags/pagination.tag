<%@tag pageEncoding="UTF-8" %>
<%@ attribute name="page" type="org.springframework.data.domain.Page" required="true" %>
<%@ attribute name="paginationSize" type="java.lang.Integer" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    int current=page.getNumber()+1;
    int begin=Math.max(1,current-paginationSize/2);
    int end=Math.min(begin+(paginationSize-1),page.getTotalPages());
    request.setAttribute("current",current);
    request.setAttribute("begin",begin);
    request.setAttribute("end",end);
%>

<div class="col-md-5 col-sm-12">
    <div class="pull-left">
        从${current*pageSize-pageSize} 到
        <c:if test="${page.totalElements >= pageSize}">
            ${current*pageSize}
        </c:if>
        <c:if test="${page.totalElements < pageSize}">
            ${page.totalElements}
        </c:if>
        /  共 ${page.totalElements} 条数据
        <select name="pageSize" id="pageSize" size="1" class="form-control input-inline input-xsmall input-sm">
            <option value="10">10</option>
            <option value="15">15</option>
            <option value="20">20</option>
            <option value="50">50</option>
        </select>
    </div>
</div>
<div class="pull-right">
    <ul class="pagination pagination-sm">
        <% if(page.hasPreviousPage()){%>
        <li><a href="?page=1&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">&lt;&lt;</a></li>
        <li><a href="?page=${current-1}&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">&lt;</a></li>
        <%}else{%>
        <li class="disabled"><a href="#">&lt;&lt;</a></li>
        <li class="disabled"><a href="#">&lt;</a></li>
        <%} %>

        <c:forEach var="i" begin="${begin}" end="${end}">
            <c:choose>
                <c:when test="${i == current}">
                    <li class="active"><a href="?page=${i}&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">${i}</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="?page=${i}&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">${i}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <% if(page.hasNextPage()){%>
        <li><a href="?page=${current+1}&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">&gt;</a></li>
        <li><a href="?page=${page.totalPages}&sortType=${sortType}&${searchParams}&pageSize=${pageSize}">&gt;&gt;</a></li>
        <%}else{%>
        <li class="disabled"><a href="#">&gt;</a></li>
        <li class="disabled"><a href="#">&gt;&gt;</a></li>
        <%} %>

    </ul>
</div>
<script>
    $("#pageSize").change(function(){
        window.location.href = "?page=1&sortType=${sortType}&${searchParams}&pageSize="+$(this).val();
    });

    var pageSize = "${pageSize}";
    $("#pageSize").find("option[value='"+pageSize+"']").attr("selected",true);
</script>