<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css">
</head>
<body>

<div class="wrapper">

    <h2>Please, login</h2>

    <form name="loginForm" method="post" action="/login">
        <label>
            Login:
            <input type="text" name="login" placeholder="Login">
        </label>
        <input type="submit">
    </form>

</div>

</body>
</html>
