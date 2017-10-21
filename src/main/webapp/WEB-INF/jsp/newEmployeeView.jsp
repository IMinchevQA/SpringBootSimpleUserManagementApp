<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
    <link rel="stylesheet" type ="text/css" href="../../css/style.css">
    <title>Add new user</title>
</head>
<body>
    <a class="goToAllEmployees" href="/listEmployees.html">All Employees</a>
    <h1>Add new employee</h1>
    <form:form modelAttribute="form">
        <form:errors path="" element="div" />
        <div>
            <form:label path="employeeNumber">Employee Number:</form:label>
            <form:input path="employeeNumber"
                        type="text"
                        pattern="^[0-9]{5,15}$"
                        maxlength="15"
                        required="required"/>
            <form:errors path="employeeNumber" />
        </div>
        </br>
        <div>
            <form:label path="firstName">First Name:</form:label>
            <form:input path="firstName"
                        tyep="text"
                        pattern="[A-Za-z]{2, 15}"
                        maxlength="15"
                        required="required" placeholder="Stamat"/>
            <form:errors path="firstName" />
        </div>
        </br>
        <div>
            <form:label path="middleInitial">Middle Initial:</form:label>
            <form:input path="middleInitial"
                        maxlength="1"
                        style="text-transform:uppercase"
                        required="required" placeholder="s"/>
            <form:errors path="middleInitial" />
        </div>
        </br>
        <div>
            <form:label path="lastName">Last Name:</form:label>
            <form:input path="lastName"
                        type="text"
                        pattern="[A-Za-z]{2, 15}"
                        maxlength="15"
                        required="required" placeholder="Stamatov"/>
            <form:errors path="lastName" />
        </div>
        </br>
        <div>
            <form:label path="departmentID">Department Id:</form:label>
            <form:input path="departmentID"
                        type="text"
                        pattern="^[0-9]{3,10}$"
                        maxlength="10"
                        required="required" placeholder="2354352254"/>
            <form:errors path="departmentID" />
        </div>
        </br>
        <div>
            <form:label path="phoneNumber">Phone Number:</form:label>
            <form:input path="phoneNumber"
                        type="text"
                        pattern="^[0-9]{5,15}$"
                        maxlength="15"
                        required="required" placeholder="878677867557657"/>
            <form:errors path="phoneNumber" />
        </div>
        </br>
        <div>
            <form:label path="dateOfHire">Date Of Hire:</form:label>
            <fmt:formatDate pattern="MM/dd/yyyy" value="${employee.dateOfHire}" var="dateOfHire"/>
            <form:input path="dateOfHire" value="${dateOfHire}"/>
            <form:errors path="dateOfHire" />
        </div>
        </br>
        <%--<div>--%>
            <%--<form:label path="job">Job:</form:label>--%>
            <%--<form:input path="job" type="text" pattern="[A-Za-z\-\s]{2,30}" required="required"/>--%>
            <%--<form:errors path="job" />--%>
        <%--</div>--%>
        <%--</br>--%>
        <%--<div>--%>
            <%--<form:label path="formalEducationYears">Formal Education Years:</form:label>--%>
            <%--<form:input path="formalEducationYears" type="text" pattern="^[0-9]{1,2}$" required="required"/>--%>
            <%--<form:errors path="formalEducationYears" />--%>
        <%--</div>--%>
        <%--</br>--%>
        <%--<div>--%>
            <%--<form:label path="sex">Sex</form:label>--%>
            <%--<form:input path="sex"--%>
                        <%--style="text-transform:uppercase"--%>
                        <%--maxlength="1"--%>
                        <%--required="required"/>--%>
            <%--<form:errors path="sex" />--%>
        <%--</div>--%>
        <%--</br>--%>
        <%--<div>--%>
            <%--<form:label path="lastName">Last Name</form:label>--%>
            <%--<form:input path="lastName" minlength="5" required="required"/>--%>
            <%--<form:errors path="lastName" />--%>
        <%--</div>--%>

        <div>
            <input type="submit" value="Add user" />
        </div>
    </form:form>
</body>
</html>