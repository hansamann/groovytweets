<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta name="layout" content="groovytweets" />
        <title>groovytweets ::: groovy in the twitter universe</title>

        <script>
          var loader = new YAHOO.util.YUILoader({

              require: ["logger", "dom", "event", "json", "connection","animation", "reset", "fonts", "grids"],
              loadOptional: true,
              onSuccess: function() {
                  YAHOO.widget.Logger.enableBrowserConsole();
                  YAHOO.namespace("gt")
              },
              timeout: 10000,
              combine: true
          });

          loader.insert();

        </script>

    </head>


    <body id="body">
    <g:if test="tweets">
      <div id="tweetWrapper${tweets[0]?.statusId}">
      <g:render template="tweets" model="['tweets':tweets]"/>
      </div>
    </g:if>
    </body>
</html>
