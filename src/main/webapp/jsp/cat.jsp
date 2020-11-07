<%--@elvariable id="cat" type="com.netcracker.cats.model.Cat"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cat info</title>
    <link rel="stylesheet" href="../css/style.css" type="text/css">
</head>
<body>


<p>Id: ${cat.id}</p>
<p>Name: ${cat.name}</p>
<p>Color: ${cat.color}</p>
<p>Age: ${cat.age}</p>
<p>Gender: ${cat.gender}</p>

<p>
    Father:
    <c:if test="${cat.father != null}">
        ${cat.father.id}
        <a href="<c:url value="/cats?id=${cat.father.id}"/>">
                ${cat.father.name}
        </a>
    </c:if>
    <c:if test="${cat.father == null}">
        Unknown
    </c:if>
</p>

<p>
    Mother: ${cat.mother.id}
    <a href="<c:url value="/cats?id=${cat.mother.id}"/>">
        ${cat.mother.name}
    </a>
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

<form action="<c:url value="/jsp/updateCat.jsp"/>" method="post">
    <input type="hidden" name="id" value="${cat.id}">
    <input type="hidden" name="name" value="${cat.name}">
    <input type="hidden" name="color" value="${cat.color}">
    <input type="hidden" name="gender" value="${cat.gender}">
    <input type="submit" value="Edit">
</form>

<form action="/cats" method="post">
    <input type="hidden" name="_method" value="delete">
    <input type="hidden" name="id" value="${cat.id}">
    <input type="submit" value="Delete">
</form>

</body>
</html>
