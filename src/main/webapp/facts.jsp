<%--
  Created by IntelliJ IDEA.
  User: a10602
  Date: 2024/2/7
  Time: 10:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Two Facts of <%= request.getAttribute("state")%></title>
</head>
    <style>
        h1 {
            text-align: center;
        }
        p {
            text-align: center;
        }
        img {
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
    </style>
    <body>
        <h1>Two Facts of the State:</h1>
        <p><b>State:</b> <%= request.getAttribute("state")%></p>
        <p><b>Capital of the state:</b> <%= request.getAttribute("stateCapital")%></p>
        <p><b>Governor of the state:</b> <%= request.getAttribute("stateGovernor")%></p>
        <p>Credit: <a href="https://en.wikipedia.org/">https://en.wikipedia.org/</a></p>
    </body>
</html>
