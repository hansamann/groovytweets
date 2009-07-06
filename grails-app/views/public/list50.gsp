<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta name="layout" content="groovytweets" />
        <title>groovytweets ::: groovy in the twitter universe</title>

        <script>
          var loader = new YAHOO.util.YUILoader({

              require: ["logger", "dom", "event", "json", "container","connection","animation", "reset", "fonts", "grids"],
              loadOptional: true,
              onSuccess: function() {
                  YAHOO.widget.Logger.enableBrowserConsole();
                  YAHOO.namespace("gt")
                  YAHOO.gt.latestStatusId = ${tweets[0].statusId}
                  YAHOO.lang.later(60000, this, pullTweets, null, true);
                  YAHOO.lang.later(30000, this, function() {
                    YAHOO.lang.later(60000, this, pullRelevances, null, true);
                  }, null, false);

                  YAHOO.lang.later(500, this, initInfoOverlay, null, false);
              },
              timeout: 10000,
              combine: true
          });

          loader.insert();

          function initInfoOverlay()
          {
            YAHOO.log('initInfoOverlay()');
            YAHOO.gt.info = new YAHOO.widget.Overlay("info");
            YAHOO.gt.info.render();
            YAHOO.gt.info.hide();

            //positioning
            //myOverlay.cfg.setProperty("x", 100);
            //myOverlay.cfg.setProperty("y", 200);


            //register onMouseOver and onMouseOut on all user icons
            //get all images of class userImage
            var userImages = YAHOO.util.Dom.getElementsByClassName('userImage', 'img', 'tweetWrapper' + YAHOO.gt.latestStatusId, function(element) {
              //YAHOO.log(element);
              YAHOO.util.Event.on(element, 'mouseover', showOverlay);
              YAHOO.util.Event.on(element, 'mouseout', hideOverlay);
            });
            YAHOO.log('added events to ' + userImages.length + ' images');

          }

          function showOverlay()
          {
            //YAHOO.log('showOverlay: ' + this.src);
            YAHOO.util.Dom.setStyle('info', 'opacity', '0');
            YAHOO.gt.info.cfg.setProperty("context", [this, "tl", "bl"]);
            YAHOO.gt.info.show();

            //animate opacity
            var anim = new YAHOO.util.Anim('info', {
              opacity: { to: 1 }
            }, 0.5, YAHOO.util.Easing.easeOut);
            anim.animate();

          }

          function hideOverlay()
          {
            //YAHOO.log('hideOverlay: ' + this.src);
            var anim = new YAHOO.util.Anim('info', {
              opacity: { to: 0 }
            }, 0.25, YAHOO.util.Easing.easeOut);
            anim.onComplete.subscribe(function() { YAHOO.gt.info.hide(); });
            anim.animate();
          }

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

    <!-- Info Overlay -->
    <div id="info">
      <div id="inforect" class="bd">
        <img id="infoarrow" src="${resource(dir:'images/groovytweets',file:'infoarrow.png')}"/>
        place content place content here
        place content here content here
        place content place content here
        place content here content here
         
      </div>
    </div>


    </body>
</html>
