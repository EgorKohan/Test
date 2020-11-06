<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 07.11.2020
  Time: 0:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cat info</title>
</head>
<body>


<p>Id: ${cat.id}</p>
<p>Name: ${cat.name}</p>
<p>Color: ${cat.color}</p>
<p>Age: ${cat.age}</p>
<p>Gender: ${cat.gender}</p>

<p>
    Father: ${cat.father.id}
    ${cat.father.name}
</p>

<p>
    Mother: ${cat.mother.id}
    ${cat.mother.name}
</p>

<table>
    <thead>
    <td>
        Id
    </td>
    <td>
        Name
    </td>
    </thead>
    <c:forEach items="${cat.children}" var="child">
        <tr>
            <td>
                    ${child.id}
            </td>
            <td>
                    ${child.name}
            </td>
        </tr>
    </c:forEach>
</table>


</body>
</html>
