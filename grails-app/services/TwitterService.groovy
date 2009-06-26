import org.codehaus.groovy.grails.commons.ConfigurationHolder
import twitter4j.*
import org.groovytweets.*
import javax.cache.*;



class TwitterService {

    boolean transactional = true
    def memcacheService

    def getTweets() {

        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        def stat = twitter.rateLimitStatus()
        log.info ("remainingHits ${stat.remainingHits}/ hourlyLimit ${stat.hourlyLimit}/ resetTimeInSeconds ${stat.resetTimeInSeconds}}")

        def tweets = []

        def paging
        def memKey = 'lastKnownStatusId'
        if (memcacheService.containsKey(memKey))
        {
            paging = new Paging((Long)memcacheService.get('lastKnownStatusId'))
        }
        else
            paging = new Paging()

        def statuses = twitter.getFriendsTimeline(paging)

        //it seems the first in teh list is always the latest, so we can save this computation?
        def latest = statuses.max {a,b-> a == b? 0: a.id<b.id ? -1: 1}
	if (latest) {
		log.info "Latest Status: ${latest.id} - ${latest.user.screenName} - ${latest.text}"
		//set latest status id in memcache
                memcacheService.put(memKey, (Long)latest.id)
	}

        statuses.each
        {
            def tweet = new org.groovytweets.Tweet()
            tweet.statusId = it.id
            tweet.statusText = it.text
            tweet.userRealName = it.user.name
            tweet.userScreenName = it.user.screenName
            tweet.userImage = it.user.profileImageURL
            tweet.userId = it.user.id
            tweets << tweet
        }

        return tweets
    }

    def getAllFriends(page = 1)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        def paging = new Paging(page)
        def users = twitter.getFriends(paging)

        if (users.size() < 99)
            return users
        else
            return users + getAllFriends(page+1)

    }

    def getUserTimeline(screenName)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)
        def stat = twitter.rateLimitStatus()
        //we check for new statuses once per minute
        //we update the friends list once per hour
        //total: 61 per hour, be safe, choose 65
        if (stat.remainingHits > 65)
        {
            def statuses = twitter.getUserTimeline(screenName, new Paging(count:200))
            def tweets = []
            statuses.each
            {
                def tweet = new org.groovytweets.Tweet()
                tweet.statusId = it.id
                tweet.statusText = it.text
                tweet.userRealName = it.user.name
                tweet.userScreenName = it.user.screenName
                tweet.userImage = it.user.profileImageURL
                tweet.userId = it.user.id
                tweets << tweet
            }
            return tweets
            
        }
        else
            return []
    }

    def follow(screenName)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)
        
        def user
        
        try
        {
            user = twitter.createFriendship(screenName)
            def status = twitter.updateStatus("groovytweets is now following @${screenName}, welcome ${user.name}! Check out http://www.groovytweets.org")
            return user
        }
        catch (TwitterException e)
        {
            log.warn("Unable to follow ${screenName}, already following?", e)
            return null
        }
    }

    def updateStatuses(statusTexte)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        statusTexte.each
        { statusText
            def status = twitter.updateStatus(statusText)
        }
    }

    def updateStatus(statusText)
    {
        updateStatuses([statusText])
    }

}
