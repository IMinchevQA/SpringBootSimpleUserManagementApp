<%@ page import="com.javainuse.model.Employee" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="../../css/style.css">
    <title>Edit employee</title>
</head>
<br>
<body>
<a class="goToAllEmployees" href="/listEmployees.html">All Employees</a>
<h1>Edit employee</h1>
<%
    Employee employee = (Employee)request.getAttribute("employeeForEdit");
    String hireDate = employee.getDateOfHire().toString().split(" ")[0];
    String [] hireDateSplit = hireDate.split("-");
    String hireYear = hireDateSplit[0];
    String hireMonth = hireDateSplit[1];
    String hireDay = hireDateSplit[2];
    String hireDateFormatted = hireMonth + "/" + hireDay + "/" + hireYear;
%>
<form:form modelAttribute="employeeForEdit">
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
                    required="required"/>
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
        <form:input path="dateOfHire" value="<%=hireDateFormatted%>"/>
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
        <input type="submit" value="Edit employee" />
    </div>
</form:form>
</body>
</html>