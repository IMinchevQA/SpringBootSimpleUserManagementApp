<%@ page import="com.javainuse.model.Employee" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%--<link rel="stylesheet" href="../../css/style.css">--%>
    <title>Show All Users</title>
</head>
<body>
    <h1>Employees page</h1>
    <table border="1">
        <thead>
            <tr>
                <td colspan="11" align="center" style="background-color: teal"><b>All Users</b></td>
            </tr>
            <tr style="background-color:lightgrey">
                <th>User Id</th>
                <th>Employee number</th>
                <th>First Name</th>
                <th>Middle Initial</th>
                <th>Last Name</th>
                <th>Department id</th>
                <th>Phone number</th>
                <th>Date of hire</th>
                <th>Job</th>
                <th colspan=2>Action</th>
        </thead>
        <tbody>
            <%
                int count = 0;
                String color = "#F9EBB3";
                if (request.getAttribute("employees") != null) {
                    List<Employee> employeesList = (ArrayList) request.getAttribute("employees");
                    Iterator it = employeesList.iterator();
                    while(it.hasNext()) {
                        color = count % 2 == 0 ? "#eeffee" : "#F9EBB3";
                        count++;
                        Employee employee = (Employee) it.next();
                        String emplDateOfHire = employee.getDateOfHire().toString().split(" ")[0];
            %>
            <tr style="background-color: <%=color%>">
                <td><%=employee.getId()%></td>
                <td><%=employee.getEmployeeNumber()%></td>
                <td><%=employee.getFirstName()%></td>
                <td><%=employee.getMiddleInitial()%></td>
                <td><%=employee.getLastName()%></td>
                <td><%=employee.getDepartmentID()%></td>
                <td><%=employee.getPhoneNumber()%></td>
                <td><%=emplDateOfHire%></td>
                <td><%=employee.getJob()%></td>
                <td><a href="editDelete/edit/<%=employee.getId()%>">Edit</a></td>
                <td><a href="editDelete/delete/<%=employee.getId()%>">Delete</a></td>
            </tr>
            <%
                    }
                }
                if (count == 0) {
            %>
            <tr>
                <td colspan=10 align="center"
                    style="background-color:#eeffee"><b>No Records Found..</b></td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
    <p><a class="add" href="/addNewEmployee.html">Add User</a></p>
</body>
</html>