<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Update cat</title>
    <link rel="stylesheet" href="<c:url value="/css/formStyle.css"/>" type="text/css">
</head>
<body>

<form id="editCatForm" name="create_edit_form" method="post" action="<c:url value="/cats"/>">
    <input name="id" value="${param.id}" type="hidden">
    <label> Name:
        <input name="name" type="text" placeholder="Name" minlength="2" maxlength="15" checked value="${param.name}">
    </label>
    <label> Age:
        <input name="age" type="number" min="0" max="20" placeholder="Age" value="${param.age}">
    </label>
    <label>
        <input name="color" type="text" placeholder="Color" minlength="2" maxlength="15" value="${param.color}">
    </label>
    <label> Male:
        <input type="radio" name="gender" value="male" disabled>
    </label>
    <label> Female:
        <input type="radio" name="gender" value="female" disabled>
    </label>
    <label> Father:
        <select name="father" disabled>
            <option value="0" selected>None</option>
            <%--@elvariable id="males" type="java.util.List"--%>
            <c:forEach items="${males}" var="father">
                <option value="${father.id}">${father.name}</option>
            </c:forEach>
        </select>
    </label>
    <label> Mother:
        <select name="mother" disabled>
            <option value="0" selected>None</option>
            <%--@elvariable id="females" type="java.util.List"--%>
            <c:forEach items="${females}" var="mother">
                <option value="${mother.id}">${mother.name}</option>
            </c:forEach>
        </select>
    </label>
    <input type="hidden" name="_method" value="put">
    <input class="button" type="submit" value="Submit">
    <a class="button" href="<c:url value="/cats"/>">Cancel</a>
</form>

</body>
</html>
