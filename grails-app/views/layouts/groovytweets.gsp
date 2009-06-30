<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yuiloader/yuiloader-min.js" ></script>
    <link rel="alternate" type="application/rss+xml" title="RSS Important Tweets" href="http://feeds.groovytweets.org/importantgroovytweets" />
    <link rel="shortcut icon" href="${resource(dir:'images',file:'gticon.png')}" type="image/x-icon" />
    <g:layoutHead />

    <title><g:layoutTitle default="groovytweets ::: groovy in the twitter universe" /></title>
    <link rel="stylesheet" href="${resource(dir:'css',file:'groovytweets.css')}" />
</head>
<body>
  <script type="text/javascript" src="${resource(dir:'js', file:'ga.js')}"></script>
  <div id="doc" class="yui-t4">
    <div id="hd">
      <!-- header -->
      <img id="logo" src="${resource(dir:'images/groovytweets',file:'groovytweets_logo.png')}"/>
    </div>
    <div id="bd">
      <!-- body -->
      <div id="yui-main">
        <div class="yui-b main">
          <g:layoutBody />
        </div>
      </div>
      <div class="yui-b sidebar">
        <script type="text/javascript"><!--
        google_ad_client = "pub-5670736856959838";
        /* 180x150, created 6/17/09 */
        google_ad_slot = "8867061989";
        google_ad_width = 180;
        google_ad_height = 150;
        //-->
        </script>
        <script type="text/javascript"
        src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
        </script>
        <br/>
        <g:link action="list50" class="linkitem">Latest Tweets</g:link>
        <g:link action="listImportant" class="linkitem">Important Tweets</g:link>
        <g:link action="friends" class="linkitem">Friends</g:link>
        <g:link action="about" class="linkitem">About</g:link>
        <br/>
        <a href="http://feeds.groovytweets.org/latestgroovytweets" target="_blank" class="linkitem">RSS Latest</a>
        <a href="http://feedburner.google.com/fb/a/mailverify?uri=latestgroovytweets&amp;loc=en_US" target="_blank" class="linkitem">Subscribe by Email</a>
        <a href="http://feeds.groovytweets.org/latestgroovytweets"><img src="http://feeds.feedburner.com/~fc/latestgroovytweets?bg=FF3300&amp;fg=000033&amp;anim=1" height="26" width="88" style="border:0" alt="" /></a>
        <br/>
        <a href="http://feeds.groovytweets.org/importantgroovytweets" target="_blank" class="linkitem">RSS Important</a>
        <a href="http://feedburner.google.com/fb/a/mailverify?uri=importantgroovytweets&amp;loc=en_US" target="_blank" class="linkitem">Subscribe by Email</a>
        <a href="http://feeds.groovytweets.org/importantgroovytweets"><img src="http://feeds.feedburner.com/~fc/importantgroovytweets?bg=FF3300&amp;fg=000033&amp;anim=1" height="26" width="88" style="border:0" alt="" /></a>
      </div>
    </div>
    <div id="ft" class="footer">
      <!-- footer -->
    groovytweets v<g:meta name="app.version"/> on Grails <g:meta name="app.grails.version"/> with app-engine plugin v<g:meta name="plugins.app-engine"/> | by <a href="http://www.svenhaiges.de">Sven Haiges</a> | follow me <a href="http://twitter.com/hansamann">@hansamann</a>
    </div>
  </div>

</body>
</html>