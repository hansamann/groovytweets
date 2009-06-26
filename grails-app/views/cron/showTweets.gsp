<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Sample title</title>
  </head>
  <body>
  <g:each in="${tweets}" var="t">
    [${t.id}, ${t.added}] ${t.userScreenName}: ${t.statusText} (${t.statusId})<br/>
  </g:each>

  </body>
</html>
