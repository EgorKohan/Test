<%--@elvariable id="cat" type="com.netcracker.cats.model.Cat"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cat info</title>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css">
</head>
<body>

<c:import url="/logo/logo.html"/>

<div class="wrapper">

    <div id="catInfo">

        <p class="info">Id: <span>${cat.id}</span></p>
        <p class="info">Name: <span>${cat.name}</span></p>
        <p class="info">Color: <span>${cat.color}</span></p>
        <p class="info">Age: <span>${cat.age}</span></p>
        <p class="info">Gender: <span>${cat.gender}</span></p>

        <p class="info">
            Father:
            <c:if test="${cat.father != null}">
                <span>
                    <a hidden href="<c:url value="/cats?id=${cat.father.id}"/>">
                            ${cat.father.name}</a>
                    </span>
            </c:if>
            <c:if test="${cat.father == null}">
                <span>Unknown</span>
            </c:if>
        </p>

        <p class="info">
            Mother:
            <c:if test="${cat.mother != null}">
                <span>
                    <a hidden href="<c:url value="/cats?id=${cat.mother.id}"/>">
                            ${cat.mother.name}
                    </a>
                   </span>
            </c:if>
            <c:if test="${cat.mother == null}">
                <span>Unknown</span>
            </c:if>
        </p>

        <table class="catTable">

            <thead>
            <tr>
                <td colspan="2">
                    Children
                </td>
            </tr>
            </thead>
            <tr>
                <td>Id</td>
                <td>Name</td>
            </tr>
            <c:forEach items="${cat.children}" var="child">
                <tr>
                    <td>
                            ${child.id}
                    </td>
                    <td>
                        <a href="<c:url value="/cats?id=${child.id}"/>">
                                ${child.name}
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <div class="buttonBar">
            <form action="<c:url value="/update"/>" method="get">
                <input type="hidden" name="id" value="${cat.id}">
                <input class="button" type="submit" value="Edit">
            </form>

            <form action="<c:url value="/cats"/>" method="post">
                <input type="hidden" name="_method" value="delete">
                <input type="hidden" name="id" value="${cat.id}">
                <input class="button" type="submit" value="Delete">
            </form>
        </div>

    </div>

</div>


</body>
</html>
