
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
    
    <g:form name="queryform" controller="play" action="query">
      <g:textArea name="jpaquery" value="select tweet from org.groovytweets.Tweet tweet where tweet.userScreenName = 'hansamann' order by tweet.importance desc" style="width:99%;height:10em;"/>
      <g:actionSubmit value="Run query..." action="query" />
    </g:form>
    </body>
</html>
