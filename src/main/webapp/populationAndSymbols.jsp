<%--
  Created by IntelliJ IDEA.
  User: Tongren Chen
  Date: 2024/2/6
  Time: 10:03 PM
  Provide information of the choosen state.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Information of <%= request.getAttribute("state")%></title>
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
        <h1>State Population & Official Symbols:</h1>
        <p><b>State:</b> <%= request.getAttribute("state")%></p>
        <p><b>Population:</b> <%= request.getAttribute("statePopulation")%></p>
        <p>Credit: Census Bureau at <a href="https://www.census.gov"> https://www.census.gov</a></p>
        <p><b>State Flag:</b> </p>
        <img src="<%= request.getAttribute("stateFlag")%>" alt="State Flag of <%= request.getAttribute("state")%>">
        <p>Credit: <%= request.getAttribute("stateFlagCredit")%> at <a href="https://en.wikipedia.org/"> https://en.wikipedia.org/</a></p>
        <p><b>State Seal:</b> </p><br>
        <img src="<%= request.getAttribute("stateSeal")%>" alt="State Seal of <%= request.getAttribute("state")%>">
        <p>Credit: <%= request.getAttribute("stateSealCredit")%> at <a href="https://en.wikipedia.org/"> https://en.wikipedia.org/</a></p>
    </body>
</html>
