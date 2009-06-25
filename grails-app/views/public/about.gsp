
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
        influencial people in the groovy community and filters their tweets for relevant content. This
        means you do not have to follow 150+ people and get spammed by irrelevant messages; instead just check
        groovytweets a couple of times a day to get your dose of groovy news.<br/>
        Besides filtering, retweets within this community are used as a relevancy indicator. A message that has been
        retweetet often is more important and visually highlighted. Current friends of groovytweets are regularly
        scanned for @replies to other usernames. If a certain treshold has been reached, these new usernames are
        being followed automatically. This ensures we keep following influencial people, even if they change
        over time.
      </p>

      <h1>How does groovytweets filter?</h1>
      <p>Plain regular expressions, this step is really quite simple. We try to match words like 'groovy', 
      'grails' or 'griffon' and a couple more that are relevant. Once a tweet meets this criteria, it is added 
      to the tweet database and shown on the groovytweets homepage.
      </p>

      <h1>What retweet formats are supported?</h1>
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
      Third, start following <a target="_blank" href="http://twitter.com/groovytweets">@groovytweets</a>. The plan
      is to RT the top messages once a day or after a tweet has achieved a certain relevance level. Be sure to get
      those tweets. 
      </p>
    </div>
    </body>
</html>
