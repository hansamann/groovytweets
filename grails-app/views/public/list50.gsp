<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta name="layout" content="groovytweets" />
        <title>groovytweets ::: groovy in the twitter universe</title>

        <script>
          var loader = new YAHOO.util.YUILoader({

              require: ["logger", "yahoo", "dom", "event", "json", "container","connection","animation", "reset", "fonts", "grids"],
              loadOptional: true,
              onSuccess: function() {
                  YAHOO.widget.Logger.enableBrowserConsole();
                  YAHOO.namespace("gt")
                  YAHOO.gt.latestStatusId = ${tweets[0].statusId}
                  YAHOO.lang.later(60000, this, pullTweets, null, true);
                  YAHOO.lang.later(30000, this, function() {
                    YAHOO.lang.later(60000, this, pullMeta, null, true);
                  }, null, false);

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


          function pullTweets()
          {
            YAHOO.log("Pulling new tweets into UI, latest statusId: " + YAHOO.gt.latestStatusId);

            if (!YAHOO.gt.latestStatusId)
              return;

            var callback =
            {
              success: function(o) {
                //insert o.responseText before first tweet
                if (o.responseText != "-1")
                {
                  //add new tweets to beginning of current list
                  var node = document.createElement('div');
                  var latest = o.getResponseHeader['latestStatusId'];
                  var tweetWrapperId = 'tweetWrapper' + latest;
                  node.setAttribute('id', tweetWrapperId);
                  node.innerHTML = o.responseText;
                  var success = YAHOO.util.Dom.insertBefore(node, YAHOO.util.Dom.get('tweetWrapper' + YAHOO.gt.latestStatusId));
                  if (success)
                  {
                    var userImages = YAHOO.util.Dom.getElementsByClassName('userImage', 'img', tweetWrapperId, function(element) {
                      //YAHOO.log(element);
                      YAHOO.util.Event.on(element, 'mouseover', showOverlay);
                      YAHOO.util.Event.on(element, 'mouseout', hideOverlay);
                    });
                    YAHOO.log('added events to ' + userImages.length + ' images');
                  }
                  else
                    YAHOO.log("something went wrong, cannot add new tweets.");


                  
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

          function pullMeta()
          {
            var tweetDivs = YAHOO.util.Dom.getElementsByClassName('tweet', 'div')
            var tweetCount = tweetDivs.length
            YAHOO.log('Pulling relevance for ' + tweetCount + ' tweets.')

            var callback =
            {
              success: function(o) {
                var meta;
                
                try {
                    meta = YAHOO.lang.JSON.parse(o.responseText);
                }
                catch (e) {
                    YAHOO.log('Unable to parse statusId/relevance map');
                    return;
                }

                for (pos in meta)
                {
                  var statusId = meta[pos][0];
                  var relevance = meta[pos][1];
                  var prettyAdded = meta[pos][2];

                  //update relevance
                  var tweetNode = YAHOO.util.Dom.get('tweet'+statusId);
                  var metaNode = YAHOO.util.Dom.get('meta'+statusId);
                  if (!tweetNode || !metaNode)
                  {
                    YAHOO.log('Unable to find tweet/meta ' + statusId + '... tweet added to db since last tweet poll?');
                  }
                  else
                  {
                    //update relevances
                    //YAHOO.log('Updating tweet' + statusId);
                    for (var i = relevance; i >= 0; i--)
                    {
                      //YAHOO.log("Removing class importance" + i);
                      YAHOO.util.Dom.removeClass(tweetNode, 'importance'+i);
                    }

                    YAHOO.util.Dom.addClass(tweetNode, 'importance'+ relevance);
                    //YAHOO.log('Adding class importance' + relevance);

                    //update meta
                    metaNode.innerHTML = prettyAdded;
                  }


                }

              },
              failure: function(o) { YAHOO.log('Oops. Something went wrong, cannot pull relevance...'); },
              argument: null
            }


            var transaction = YAHOO.util.Connect.asyncRequest('GET', '/public/pullMeta/'+ tweetCount, callback);

          }

        </script>

    </head>


    <body id="body">
    <div id="tweetWrapper${tweets[0].statusId}">
    <g:render template="tweets" model="['tweets':tweets]"/>
    </div>

    <g:render template="infobox"/>


    </body>
</html>
