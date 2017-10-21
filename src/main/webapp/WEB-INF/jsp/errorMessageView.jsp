
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="../../css/style.css">
    <title>Show All Users</title>
</head>
    <br>
    <body style="color:red">
        <h1>ERROR PAGE</h1>
        <%
            System.out.println("ERROR PAGE");
                String message = (String) request.getAttribute("error");
                message = message.substring(0,1).toUpperCase() + message.substring(1);
        %>
        <p><%=message%></p>
    </body>
</html>

