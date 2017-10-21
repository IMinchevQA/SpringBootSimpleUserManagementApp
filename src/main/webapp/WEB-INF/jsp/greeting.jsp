
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="../../css/style.css">
    <title>Greeting Page</title>
</head>
<br>
<body>
<h1>Greeting PAGE</h1>
<p>
    Hello <b><%=request.getAttribute("personForGreeting")%></b>
    <h2>Form</h2>
    <form>
        <input type="text" name="name" />
        <input type="submit" value="Send a greeting"/>
    </form>
</p>
</body>
</html>

