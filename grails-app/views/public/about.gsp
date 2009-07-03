
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
    <div class="aboutWrapper">
      <p>groovytweets is an effort to provide the groovy community with up to date news via twitter.
        <a target="_blank" href="http://twitter.com/groovytweets">@groovytweets</a> follows the most
        influencial people in the groovy community and filters their tweets for relevant content.
        <br/>
        Besides filtering, retweets within this community are used as a relevancy indicator. A message that has been
        retweetet often is likely more important and is visually highlighted on the groovytweets website. Also, current
        friends of groovytweets are regularly
        scanned for @replies to other usernames. If a certain treshold has been reached, these new usernames are
        being followed automatically. This ensures we keep following influencial people, even if they change
        over time.
      </p>

      <h1>How does groovytweets filter?</h1>
      <p>Plain regular expressions, this step is really quite simple. We try to match words like 'groovy', 
      'grails' or 'griffon' and a couple more that are relevant. Once a tweet meets this criteria, it is added 
      to the tweet database and shown on the groovytweets homepage.
      </p>

      <h1>How can I participate?</h1>
      <p>To have your retweets accepted, please first make sure that groovytweets is following you. If not, we cannot
      get your retweets and cannot use them to make the original tweet more important. If we do not follow you already,
      check the <g:link action="friends">friends page</g:link> to find out how you can suggest your own twitter username as a new friend.
      </p>
      <p>
      Once we are following you, participating and retweeting is easy. We support a couple of retweet formats, the
      most common being RT @username: &lt;original message&gt;. Please keep it a pure retweet, e.g. do not add additional
      content like extra characters to the end of the original message. We have a hard time finding the original message
      otherwise. In any case, retweet messages are not shown on groovytweets, instead we mark the original message
      more important.
      </p>
      <p>groovytweets currently understands these retweet formats (plus a few variations that we really do not want to
      recommend using, so we don't mention them at all):
      <ul>
      <li>RT @username: &lt;original message&gt;</li>
      <li>♺ @username: &lt;original message&gt;</li>
      <li>&lt;original message&gt; (via @username)</li>
      <li>&lt;original message&gt; (by @username)</li>
      </ul>

      <h1>How can I help?</h1>
      <p>First, click the ads. We currently host groovytweets as a Grails 1.1.1 app on Google's AppEngine and
      already consume about 40% of the daily compute allowance. I will invest the money in additional compute
      cycles.</p>
      <p>
      Second, just follow and twitter your groovy friends as usual. If you like something, be sure to retweet it.
      We will then automatically add it to groovytweets in case it was relevant. 
      </p>
      <p>
      Third, start following <a target="_blank" href="http://twitter.com/groovytweets">@groovytweets</a>. We currently
      'retweet' Tweets once they reach the first relevance level and of course do not count these retweets into the
      tweets we already track. This means: following groovytweets gives you the trending tweets. You should still
      participate in retweeting original tweets as this is what allows us to discover trending tweets.
      </p>
      Fourth, spread the love. See our headline animators...

      <h1>groovy love: headline animators</h1>
      <p>Feel free to include these feedburner headline animators on your own blog or site. The animators display
      the last 5 entries from the important tweets RSS feed. Thanx for supporting groovytweets!</p>
      468x60
      <p style="margin-top:10px; margin-bottom:0; padding-bottom:0; text-align:center; line-height:0"><a target="_blank" href="http://feeds.feedburner.com/~r/importantgroovytweets/~6/3"><img src="http://feeds.feedburner.com/importantgroovytweets.3.gif" alt="groovytweets ::: important tweets" style="border:0"></a></p><p style="margin-top:5px; padding-top:0; font-size:x-small; text-align:center"><a href="http://feedburner.google.com/fb/a/headlineanimator/install?id=poj4rupefrgj6m7i2imh4c5gck&amp;w=3" onclick="window.open(this.href, 'haHowto', 'width=520,height=600,toolbar=no,address=no,resizable=yes,scrollbars'); return false" target="_blank">&uarr; Grab this Headline Animator</a></p>
      234x60
      <p style="margin-top:10px; margin-bottom:0; padding-bottom:0; text-align:center; line-height:0"><a target="_blank" href="http://feeds.feedburner.com/~r/importantgroovytweets/~6/1"><img src="http://feeds.feedburner.com/importantgroovytweets.1.gif" alt="groovytweets" style="border:0"></a></p><p style="margin-top:5px; padding-top:0; font-size:x-small; text-align:center"><a href="http://feedburner.google.com/fb/a/headlineanimator/install?id=poj4rupefrgj6m7i2imh4c5gck&amp;w=1" onclick="window.open(this.href, 'haHowto', 'width=520,height=600,toolbar=no,address=no,resizable=yes,scrollbars'); return false" target="_blank">&uarr; Grab this Headline Animator</a></p>
      180x100
      <p style="margin-top:10px; margin-bottom:0; padding-bottom:0; text-align:center; line-height:0"><a target="_blank" href="http://feeds.feedburner.com/~r/importantgroovytweets/~6/2"><img src="http://feeds.feedburner.com/importantgroovytweets.2.gif" alt="groovytweets" style="border:0"></a></p><p style="margin-top:5px; padding-top:0; font-size:x-small; text-align:center"><a href="http://feedburner.google.com/fb/a/headlineanimator/install?id=poj4rupefrgj6m7i2imh4c5gck&amp;w=2" onclick="window.open(this.href, 'haHowto', 'width=520,height=600,toolbar=no,address=no,resizable=yes,scrollbars'); return false" target="_blank">&uarr; Grab this Headline Animator</a></p>
    </div>

    </body>
</html>
