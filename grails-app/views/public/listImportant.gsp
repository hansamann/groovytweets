<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta name="layout" content="groovytweets" />
        <title>groovytweets ::: groovy in the twitter universe</title>

        <script>
          var loader = new YAHOO.util.YUILoader({

              require: ["logger", "yahoo", "reset", "fonts", "grids"],
              loadOptional: true,
              onSuccess: function() {
                  YAHOO.widget.Logger.enableBrowserConsole();
                  YAHOO.namespace("gt");

                  YAHOO.gt.latestStatusId = ${tweets[0].statusId}
                  YAHOO.lang.later(500, this, initInfoOverlay, null, false);
              },
              timeout: 10000,
              combine: true
          });

          loader.addModule({
                  name: "infobox",
                  type: "js",
                  fullpath: "/js/groovytweets/infobox.js",
                  requires: ['yahoo', 'event', 'container', 'animation', 'connection', 'json', 'dom']
          });

          loader.require('infobox');

          loader.insert();

        </script>

    </head>


    <body id="body">
    <g:if test="tweets">
      <div id="tweetWrapper${tweets[0]?.statusId}">
      <g:render template="tweets" model="['tweets':tweets]"/>
      </div>
    </g:if>

    <g:render template="infobox"/>
    
    </body>
</html>
