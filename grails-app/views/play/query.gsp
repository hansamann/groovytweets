
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
    <g:each in="${tweets}" var="tweet">
    <p>${tweet.toString()}</p>
    </g:each>
 
    </body>
</html>
