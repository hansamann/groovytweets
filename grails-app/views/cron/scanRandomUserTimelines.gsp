<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Scan Results</title>
  </head>
  <body>
  
    Results for scan of ${user.screenName}, filtered list is:
    <ul>
    <g:each in="${replies}" var="reply">
      <li>${reply.key}: ${reply.value}</li>
    </g:each>
    </ul>

  </body>
</html>
