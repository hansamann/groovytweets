<g:each in="${tweets}" status="i" var="tweet">
  <div id="tweet${tweet.statusId}" class="tweet importance${tweet.importance}">
    <div class="textWrap">
      <div class="icons">
        <g:if test="${tweet.userImage}">
          <img class="userImage" src="${tweet.userImage}" alt="${tweet.userScreenName}"/>
          <a target="_blank" href="${tweet.retweetURL}">
            <img class="retweetImage" src="${resource(dir:'images/groovytweets',file:'retweet.png')}"/>
          </a>
        </g:if>
        
      </div>

      <g:if test="${tweet.hasRealName()}"
            <span class="userRealName">${tweet.userRealName},</span>
      </g:if>
      <span class="userScreenName"><a class="screenNameLink" target="_blank" href="http://twitter.com/${tweet.userScreenName}">${tweet.userScreenName}</a>:</span>
      <span class="statusText">${tweet.encodedStatusText}</span>
    </div>
    <div id="meta${tweet.statusId}" class="meta">added ${tweet.prettyAdded}</div>
  </div>
</g:each>