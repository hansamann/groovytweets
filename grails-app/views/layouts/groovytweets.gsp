<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yuiloader/yuiloader-min.js" ></script>

  <g:layoutHead />

  <title><g:layoutTitle default="groovytweets ::: groovy in the twitter universe" /></title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'groovytweets.css')}" />
  <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

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
        <g:link action="list50" class="linkitem">Latest Tweets</g:link>
        <g:link action="listImportant" class="linkitem">Important Tweets</g:link>
        <g:link action="friends" class="linkitem">Friends</g:link>
        <g:link action="about" class="linkitem">About</g:link>
      </div>
    </div>
    <div id="ft" class="footer">
      <!-- footer -->
    groovytweets 0.1 on Grails 1.1.1 | by <a href="http://www.svenhaiges.de">Sven Haiges</a> | follow me <a href="http://twitter.com/hansamann">@hansamann</a>
    </div>
  </div>

</body>
</html>