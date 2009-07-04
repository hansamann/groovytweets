<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Scan Results</title>
  </head>
  <body>
  
  <p>Results for FRIEND scan of ${user.screenName} for @mentions, filtered list is:</p>
  <ul>
  <g:each in="${replies}" var="reply">
    <li>${reply.key}: ${reply.value}</li>
  </g:each>
  </ul>

  <hr/>

  <p>All potential followers (followers-friends):</p>
  <ul>
  <g:each in="${potentialFriends}" var="potFriend">
    <li>${potFriend}</li>
  </g:each>
  </ul>

  <p>Results for FOLLOWER scan of ${potentialFriend} for groovy tweets: found ${followerGroovyTweets} groovy tweets</p>




  </body>
</html>
