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
                  YAHOO.gt.latestStatusId = ${tweets[0].statusId}
                  YAHOO.lang.later(60000, this, pullTweets, null, true);
                  YAHOO.lang.later(30000, this, function() {
                    YAHOO.lang.later(60000, this, pullRelevances, null, true);
                  }, null, false);
              },
              timeout: 10000,
              combine: true
          });

          loader.insert();

          function pullTweets()
          {
            YAHOO.log("Pulling new tweets into UI, latest statusId: " + YAHOO.gt.latestStatusId);
            var callback =
            {
              success: function(o) {
                //insert o.responseText before first tweet
                if (o.responseText != "-1")
                {
                  //add new tweets to beginning of current list
                  var node = document.createElement('div');
                  var latest = o.getResponseHeader['latestStatusId'];
                  node.setAttribute('id', 'tweetWrapper' + latest);
                  node.innerHTML = o.responseText;
                  var success = YAHOO.util.Dom.insertBefore(node, YAHOO.util.Dom.get('tweetWrapper' + YAHOO.gt.latestStatusId));
                  if (!success)
                    alert("something went wrong...");

                  //pull response header for latest status id
                  
                  YAHOO.log("New latestStatusId: " + latest);
                  YAHOO.gt.latestStatusId = latest;

                  //TODO animate fade in...
                }

              },
              failure: function(o) { YAHOO.log('Oops. Something went wrong, cannot pull new tweets.'); },
              argument: null
            }

            
            var transaction = YAHOO.util.Connect.asyncRequest('GET', '/public/pullTweets/'+ YAHOO.gt.latestStatusId, callback);

          }

          function pullRelevances()
          {
            var tweetDivs = YAHOO.util.Dom.getElementsByClassName('tweet', 'div')
            var tweetCount = tweetDivs.length
            YAHOO.log('Pulling relevance for ' + tweetCount + ' tweets.')

            var callback =
            {
              success: function(o) {
                var relevances;
                
                try {
                    relevances = YAHOO.lang.JSON.parse(o.responseText);
                }
                catch (e) {
                    YAHOO.log('Unable to parse statusId/relevance map');
                    return;
                }

                for (pos in relevances)
                {
                  var statusId = relevances[pos][0];
                  var relevance = relevances[pos][1];

                  var tweetNode = YAHOO.util.Dom.get('tweet'+statusId);

                  if (!tweetNode)
                  {
                    YAHOO.log('Unable to find tweet' + statusId + '... tweet added to db since last tweet poll?');
                    return;
                  }
                  else
                  {
                    //YAHOO.log('Updating tweet' + statusId);
                  }

                  for (var i = relevance; i >= 0; i--)
                  {
                    //YAHOO.log("Removing class importance" + i);
                    YAHOO.util.Dom.removeClass(tweetNode, 'importance'+i);
                  }

                  YAHOO.util.Dom.addClass(tweetNode, 'importance'+ relevance);
                  //YAHOO.log('Adding class importance' + relevance);
                }

              },
              failure: function(o) { YAHOO.log('Oops. Something went wrong, cannot pull relevance...'); },
              argument: null
            }


            var transaction = YAHOO.util.Connect.asyncRequest('GET', '/public/pullRelevances/'+ tweetCount, callback);

          }

        </script>

    </head>


    <body id="body">
    <div id="tweetWrapper${tweets[0].statusId}">
    <g:render template="tweets" model="['tweets':tweets]"/>
    </div>
    </body>
</html>
