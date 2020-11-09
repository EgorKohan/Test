<%--@elvariable id="cat" type="com.netcracker.cats.model.Cat"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Create cat</title>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css">
    <link rel="stylesheet" href="<c:url value="/css/formStyle.css"/>" type="text/css">
</head>
<body>

<c:import url="/logo/logo.html"/>

<div class="error" style="background-color: red; color: white; font-size: 1.5em; text-align: center">
    <%--@elvariable id="errorDescription" type="java.lang.String"--%>
    <c:if test="${errorDescription.length() != 0}">
        ${errorDescription}
    </c:if>
</div>

<div class="wrapper">

    <form id="editCatForm" name="create_edit_form" method="post" action="/cats">

        <fieldset>

            <legend>Input a cat data</legend>

            <label>
                Name:
                <input name="name" type="text" placeholder="Name" minlength="2" maxlength="15" checked
                       value="${cat.name}">
            </label>

            <label>
                Age:
                <input name="age" type="number" value="${cat.age}" min="0" max="20" placeholder="Age">
            </label>

            <label>
                Color:
                <input name="color" type="text" placeholder="Color" minlength="2" maxlength="15" value="${cat.color}">
            </label>

            <label>
                Male:
                <input type="radio" name="gender" value="male"
                <%--@elvariable id="Gender" type="com.netcracker.cats.model.Gender"--%>
                <c:if test="${cat.gender.toString().equals(\"MALE\") || cat.gender == null}">
                       checked
                </c:if>
                >
            </label>

            <label>
                Female:
                <input type="radio" name="gender" value="female"
                <%--@elvariable id="Gender" type="com.netcracker.cats.model.Gender"--%>
                <c:if test="${cat.gender.toString().equals(\"FEMALE\")}">
                       checked
                </c:if>>
            </label>

            <label>
                Father:
                <select name="father">
                    <c:if test="${cat.father != null}">
                        <option value="${cat.father.id}" selected>${cat.father.name}</option>
                        <option value="0">None</option>
                    </c:if>
                    <c:if test="${cat.father == null}">
                        <option value="0" selected>None</option>
                    </c:if>
                    <%--@elvariable id="males" type="java.util.List"--%>
                    <c:forEach items="${males}" var="father">
                        <option value="${father.id}">${father.name}</option>
                    </c:forEach>
                </select>
            </label>

            <label>
                Mother:
                <select name="mother">
                    <c:if test="${cat.mother != null}">
                        <option value="${cat.mother.id}" selected>${cat.mother.name}</option>
                        <option value="0">None</option>

                    </c:if>
                    <c:if test="${cat.mother == null}">
                        <option value="0" selected>None</option>
                    </c:if>
                    <%--@elvariable id="females" type="java.util.List"--%>
                    <c:forEach items="${females}" var="mother">
                        <option value="${mother.id}">${mother.name}</option>
                    </c:forEach>
                </select>
            </label>

            <div class="buttonBar">
                <input class="createButton button" type="submit" value="Submit">

                <a class="cancelButton button" href="/cats">Cancel</a>

            </div>

        </fieldset>

    </form>

</div>

</body>
</html>
