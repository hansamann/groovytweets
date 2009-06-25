
<html>
    <head>
        <meta name="layout" content="groovytweets" />
        <title>groovytweets ::: groovy in the twitter universe</title>

        <script>
          var loader = new YAHOO.util.YUILoader({

              require: ["logger", "reset", "fonts", "grids"],
              loadOptional: true,
              onSuccess: function() {
                  YAHOO.widget.Logger.enableBrowserConsole();
                  YAHOO.namespace("gt");
              },
              timeout: 10000,
              combine: true
          });

          loader.insert();
        </script>

    </head>


    <body id="body">
    <div class="friendsWrapper">
      <p>Below are <a target="_blank" href="http://twitter.com/groovytweets">@groovytweets</a> friends. They are ordered by the
      time they were added as a friend. If you want to be added as a friend of groovytweets, send a regular twitter message to <a target="_blank" href="http://twitter.com/groovytweets">@groovytweets</a>
      with the content 'suggest @username', where username is your twitter username.
      </p>
      <br/>
      <p>
      Total: ${friends.size()}
      </p>
      <br/>
      <g:each in="${friends}" status="i" var="user">
        <a target="_blank" href="http://www.twitter.com/${user.screenName}">${user.screenName} (${user.name})</a><g:if test="${i != (friends.size() - 1)}">, </g:if>
      </g:each>
    </div>
    </body>
</html>
