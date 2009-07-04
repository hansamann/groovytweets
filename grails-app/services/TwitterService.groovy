/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */


import org.codehaus.groovy.grails.commons.ConfigurationHolder
import twitter4j.*
import org.groovytweets.*
import javax.cache.*;


/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
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

        if (users.size() < 90) //call might return less than 100
            return users
        else
            return users + getAllFriends(page+1)

    }

    def getAllFollowers(page = 1)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        def paging = new Paging(page)
        def users = twitter.getFollowers(paging)

        if (users.size() < 90) //call might return less than 100
            return users
        else
            return users + getAllFollowers(page+1)
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



    def updateStatus(text)
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        try
        {
            def status = twitter.updateStatus(text)
            return status
        }
        catch (TwitterException e)
        {
            log.warn("Unable to update status: " + text, e)
            return null
        }

    }

    def getMentions()
    {
        def twitter = new Twitter(ConfigurationHolder.config.twitterUsername, ConfigurationHolder.config.twitterPassword)

        def stat = twitter.rateLimitStatus()

        if (stat.remainingHits > 65)
        {
            def paging
            def memKey = 'lastMentionStatusId'
            if (memcacheService.containsKey(memKey))
                paging = new Paging(sinceId:(Long)memcacheService.get(memKey), count:200)
            else
                paging = new Paging(count:200)

            def mentions = twitter.getMentions(paging)

            //find new latest
            def latest = mentions.max {a,b -> a == b ? 0 : a.id < b.id ? -1: 1}
            if (latest) {
                memcacheService.put(memKey, (Long)latest.id)
            }

            return mentions
        }
        else
        {
            log.info("Less than 65 remaining hits this hour, not getting mentions")
            return []
        }
    }

}
