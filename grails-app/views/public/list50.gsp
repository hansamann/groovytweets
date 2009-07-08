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

            //register onMouseOver and onMouseOut on all user icons
            //get all images of class userImage
            var userImages = YAHOO.util.Dom.getElementsByClassName('userImage', 'img', 'tweetWrapper' + YAHOO.gt.latestStatusId, function(element) {
              //YAHOO.log(element);
              YAHOO.util.Event.on(element, 'mouseover', showOverlay);
              YAHOO.util.Event.on(element, 'mouseout', hideOverlay);
            });
            YAHOO.log('added events to ' + userImages.length + ' images');

            YAHOO.util.Event.on('info', 'mouseover', showOverlay);
            YAHOO.util.Event.on('info', 'mouseout', hideOverlay);

          }

          function showOverlay(event, deferred)
          {
            deferred = deferred || false;
            YAHOO.log('showOverlay: deferred=' + deferred);

            if (deferred)
            {
              YAHOO.util.Dom.setStyle('infocontent', 'display', 'none');
              YAHOO.util.Dom.setStyle('waiting', 'display', 'block');

              YAHOO.util.Dom.setStyle('info', 'opacity', '0');
              YAHOO.gt.info.cfg.setProperty("context", [this, "tl", "bl"]);
              YAHOO.gt.info.show();

              //animate opacity
              var anim = new YAHOO.util.Anim('info', {
                opacity: { to: 1 }
              }, 0.5, YAHOO.util.Easing.easeOut);
              anim.animate();

              //request content
              YAHOO.log('Requesting info for: ' + this.alt);
              var callback =
              {
                success: function(o) {
                  if (o.responseText != "-1")
                  {
                    var info;
                    try {
                        info = YAHOO.lang.JSON.parse(o.responseText);
                    }
                    catch (e) {
                        YAHOO.log('Unable to parse user info map');
                        return;
                    }

                    //change content
                    YAHOO.util.Dom.get('followersCount').innerHTML = info.followersCount;
                    YAHOO.util.Dom.get('friendsCount').innerHTML = info.friendsCount;
                    YAHOO.util.Dom.get('location').innerHTML = info.location;
                    YAHOO.util.Dom.get('web').innerHTML = '<a target="_blank" href="'+info.url+'">' + info.url + '</a>';
                    YAHOO.util.Dom.get('bio').innerHTML = info.description;
                    YAHOO.util.Dom.get('followLink').innerHTML = '<a target="_blank" href="http://twitter.com/'+info.screenName+'">follow ' + info.screenName + '</a>';


                    //make infocontent visible and hide waiting
                    YAHOO.util.Dom.setStyle('infocontent', 'display', 'block');
                    YAHOO.util.Dom.setStyle('waiting', 'display', 'none');

                  }

                },
                failure: function(o) { YAHOO.log('Oops. Something went wrong, cannot pull user info.'); },
                argument: null
              }


              var transaction = YAHOO.util.Connect.asyncRequest('GET', '/public/pullInfo/'+ this.alt, callback);

            }
            else
            {
              if (YAHOO.gt.infoShowTimer)
                YAHOO.gt.infoShowTimer.cancel();
              
              if (YAHOO.gt.infoHideTimer)
                YAHOO.gt.infoHideTimer.cancel();

              if (this.id !== 'info' && YAHOO.util.Dom.getStyle('info', 'visibility') == 'hidden')
                YAHOO.gt.infoShowTimer = YAHOO.lang.later(500, this, showOverlay, [event, true], false);
            }

          }

          function hideOverlay(event, deferred)
          {
            deferred = deferred || false;
            YAHOO.log('hideOverlay: deferred=' + deferred);

            if (YAHOO.gt.infoShowTimer)
              YAHOO.gt.infoShowTimer.cancel();

            if (deferred)
            {
              var anim = new YAHOO.util.Anim('info', {
                opacity: { to: 0 }
              }, 0.25, YAHOO.util.Easing.easeOut);
              anim.onComplete.subscribe(function() { YAHOO.gt.info.hide(); });
              anim.animate();
            }
            else
            {
              if (YAHOO.gt.infoHideTimer)
                YAHOO.gt.infoHideTimer.cancel();

              YAHOO.gt.infoHideTimer = YAHOO.lang.later(500, this, hideOverlay, [event, true], false);
            }
          }

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
        <div id="waiting" style="text-align:right;">
          <img src="${resource(dir:'images/groovytweets',file:'spinner.gif')}"/>
        </div>
        <div id="infocontent" style="display:none;">
        <span class="key" id="followersCount">12345</span> followers / <span class="key" id="friendsCount">80</span> friends<br/>
        <span class="key">Location</span> <span id="location">Munich, Germany</span><br/>
        <span class="key">Web</span> <span id="web"><a href="http://graemerocher.blogspot.com/" target="_blank">http://graemerocher.blogspot.com/</a></span><br/>
        <span class="key">Bio</span> <span id="bio">Head of Grails Development at SpringSource</span>
        <div class="follow" id="followLink"><a target="_blank" href="http://twitter.com/hansamann">follow hansamann</a></div>
        </div>
      </div>
    </div>


    </body>
</html>
