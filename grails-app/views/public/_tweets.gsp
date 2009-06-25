<g:each in="${tweets}" status="i" var="tweet">
  <div id="tweet${tweet.statusId}" class="tweet importance${tweet.importance}">
    <div class="textWrap">
      <g:if test="${tweet.userImage}">
        <img class="userImage" src="${tweet.userImage}"/>
      </g:if>

      <g:if test="${tweet.hasRealName()}"
            <span class="userRealName">${tweet.userRealName},</span>
      </g:if>
      <span class="userScreenName"><a class="screenNameLink" target="_blank" href="http://twitter.com/${tweet.userScreenName}">${tweet.userScreenName}</a>:</span>
      <span class="statusText">${tweet.encodedStatusText}</span>
    </div>
  </div>
</g:each>