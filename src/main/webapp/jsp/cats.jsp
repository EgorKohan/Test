<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cats</title>
    <link rel="stylesheet" href="css/style.css" type="text/css">
</head>
<body>

<table class="catTable">
    <thead>
        <td>Id</td>
        <td>Name</td>
        <td>Color</td>
        <td>Age</td>
        <td> Gender</td>
    </thead>
    <%--@elvariable id="cats" type="java.util.List"--%>
    <c:forEach items="${cats}" var="cat">
        <tr>
            <td>${cat.id}</td>
            <td><a href="<c:url value="/cats?id=${cat.id}"/>">${cat.name}</a></td>
            <td>${cat.color}</td>
            <td>${cat.age}</td>
            <td>${cat.gender}</td>
        </tr>
    </c:forEach>
</table>

<a href="<c:url value="/create"/>">Create a new cat</a>

</body>
</html>
