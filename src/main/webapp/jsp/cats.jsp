<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cats</title>
</head>
<body>

<table>
    <tr>
        <td>Id</td>
        <td>Name</td>
        <td>Color</td>
        <td>Age</td>
        <td>Gender</td>
    </tr>
    <%--@elvariable id="cats" type="java.util.List"--%>
    <c:forEach items="${cats}" var="cat">
        <tr>
            <td>${cat.id}</td>
            <td>${cat.name}</td>
            <td>${cat.color}</td>
            <td>${cat.age}</td>
            <td>${cat.gender}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>