<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Lab4</title>
</head>
<body>
<h1><%= "Lab 4 - Milestone 1" %>
</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>
<br/>
<a href="skiers/1/seasons/2019/days/3/skiers/33">Test 1 - 200 - successful operation</a>
<br/>
<a href="skiers/1/seasons/2019//////days/3/skiers/">Test 2 - 400 - SkiersServlet Error</a>
<br/>
<a href="skiers">Test 3 - 404 - SkiersServlet urlPath == null || urlPath.isEmpty()</a>
<br/>
<a href="statistics">Test 4 - 200 - StatsServlet</a>

</body>
</html>