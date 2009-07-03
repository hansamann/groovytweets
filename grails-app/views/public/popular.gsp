
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
    <div class="popularWrapper">
      <p>The chart below visualizes the top 20 mentions within the groovytweets network. We count each
        mention of a username (like @grailspodcast) including the mentions in retweets. The resulting
        number is a good popularity indicator.
      </p>
      <g:if test="${popularityChartURL}">
        <img src="${popularityChartURL}"/>
      </g:if>
      <g:else>
        <p>Sorry, there is currently no chart available. Pleaes come back soon as we try to refresh
        the chart content every 15 minutes</p>
      </g:else>
      
    </div>
    </body>
</html>
