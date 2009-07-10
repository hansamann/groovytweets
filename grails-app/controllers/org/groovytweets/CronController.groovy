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

package org.groovytweets

import java.util.regex.Pattern
import com.google.appengine.api.datastore.*
import org.springframework.transaction.support.*
import org.springframework.orm.jpa.*

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class CronController
{

    def twitterService
    def memcacheService
    def mailService
    def jpaTemplate
    def transactionTemplate

    //consider word boundaries with \b but let's try first'
    def pattern = Pattern.compile(/groovy|grails|griffon|gr8|gr\*|gorm|gsql|gsp|\bgant|gradle|groosh|builder|plugin|launcher|xmlrpc|parallelizer/, Pattern.CASE_INSENSITIVE)

    def index = { redirect(action:'showTweets', params:params)}

    def updateTweets = {
        log.debug "updateTweets..."

        def tweets = twitterService.getTweets()
        log.info "got ${tweets.size()} tweets..." 

        def groovyTweets = tweets.findAll { it.statusText =~ pattern }

        //if there are tweets but no groovyweets were found
        if (!groovyTweets && tweets)
        {
            //update lastKnownStatusId with the latest tweet so we do not check these tweets again
            memcacheService.put('lastKnownStatusId', (Long)tweets[-1].statusId)
        }

        def added = 0
        def replyCount = 0

        groovyTweets.each { tweet ->
            if (!statusExists(tweet.statusId))
            {
                replyCount += logAt(tweet)
                def retweetResult = isRetweet(tweet)
                if (retweetResult)
                {
                    //this is a RT
                    //... is allowed to truncate oversize original messages
                    if (retweetResult.text && retweetResult.text.endsWith('...'))
                    retweetResult.text = retweetResult.text[0..-4] //remove ...

                    //find original(s)
                    def existingTweets = findExistingTweets(retweetResult.text, retweetResult.screenName)

                    //increase importance (++)
                    //should just be one tweet, change find ExistingTweets?
                    existingTweets.each
                    { existingTweet ->
                        transactionTemplate.execute( { status ->
                                def tweetInstance = jpaTemplate.find( Tweet.class, existingTweet.id )

                                if(tweetInstance)
                                {
                                    tweetInstance.importance = tweetInstance.importance + 1

                                    try{
                                        jpaTemplate.persist(tweetInstance)                                        
                                    } catch( Exception e ){
                                        render("Unable to update the original tweet for a RT match")
                                    }

                                    if (tweetInstance.importance == 1 )
                                    {
                                        twitterService.updateStatus("RT @${tweetInstance.userScreenName}: ${tweetInstance.statusText}")
                                    }

                                }
                                else {
                                    log.warn("Could not find existingTweet... should not happen...")
                                }

                            } as TransactionCallback )
                    }

                }
                else if (!tweet.statusText.startsWith('RT'))
                {
                    transactionTemplate.execute(
                        { status ->
                            jpaTemplate.persist(tweet)
                            jpaTemplate.flush()
                            added++
                        } as TransactionCallback )
                }
            }

            //with each iteration, update the lastKnownStatusId
            memcacheService.put('lastKnownStatusId', (Long)tweet.statusId)
        } //end each
        render "done - added ${added} tweets, logged @replies: ${replyCount}"
    }


    def updateFriendsAndFollowers = {
        log.debug "updateFriendsAndFollowers"

        def friends = twitterService.getAllFriends() //intializes page to 1
        def cacheFriends = friends.collect { [id:it.id, name:it.name, screenName:it.screenName, profileImageURL:it.profileImageURL, followersCount:it.followersCount, friendsCount:it.friendsCount, statusesCount:it.statusesCount, location:it.location, description:it.description, url:it.URL ]}
        memcacheService.put("friends", cacheFriends)
        
        def followers = twitterService.getAllFollowers() //intializes page to 1
        def cacheFollowers = followers.collect { [id:it.id, name:it.name, screenName:it.screenName, profileImageURL:it.profileImageURL, followersCount:it.followersCount, friendsCount:it.friendsCount, statusesCount:it.statusesCount, location:it.location, description:it.description, url:it.URL ]}
        memcacheService.put("followers", cacheFollowers)

        //user Config for this setting
        def self = twitterService.getUserDetail('groovytweets')
        memcacheService.put("self", [id:self.id, name:self.name, screenName:self.screenName, profileImageURL:self.profileImageURL, followersCount:self.followersCount, friendsCount:self.friendsCount, statusesCount:self.statusesCount, location:self.location, description:self.description, url:self.URL ])
    
        render "done - saved friends(${cacheFriends.size()}) and followers(${cacheFollowers.size()})  and own user (self) to memcache"
    }

    def scanRandomUserTimelines = {
        log.debug "scanRandomUserTimelines"

        def sendError = false
        def model = [:]

        //scan a friend public timeline for other usernames
        def friends = null
        if (memcacheService.containsKey('friends'))
        {
            friends = memcacheService.get('friends')
            def screenNameList = friends.collect { it.screenName }
            Collections.shuffle(friends)

            def user = friends[0]
            def tweets = twitterService.getUserTimeline(user.screenName)
            def groovyTweets = tweets.findAll { it.statusText =~ pattern }
            //get replymap, filter by at least two mentions AND not already in friends list in groovy tweets
            def replyMap = getReplyMap(groovyTweets).findAll { !(it.key in screenNameList) && it.value >= 3 }

            replyMap.each { screenName, mentions ->
                def newFriend = twitterService.follow(screenName)
                if (newFriend)
                {
                    mailService.sendAdminMail("[groovytweets] user scan, adding ${screenName}", "Scanned ${user.screenName}, now adding qualifying user ${screenName} based on ${mentions} mentions")
                }
                else
                {
                    mailService.sendAdminMail("[groovytweets] user scan, UNABLE to follow ${screenName}", "Scanned ${user.screenName}, tried to add ${screenName} based on ${mentions} mentions, but failed (already following?)")
                }
            }

            model.replies = replyMap
            model.user = user
           
        }
        else
        {
            sendError = true
            mailService.sendAdminMail("[groovytweets] friend public timeline scan FAILED", "memcache does not contain friends list")
        }

        //scan followers and pick a random public timeline of a user we are not yet following
        if (memcacheService.containsKey('followers') && friends)
        {
            def followers = memcacheService.get('followers')

            def friendScreenNameList = friends.collect { it.screenName }
            def followersScreenNameList = followers.collect { it.screenName }

            def potentialFriends = followersScreenNameList - friendScreenNameList
            if (potentialFriends)
            {
                Collections.shuffle(potentialFriends)
                def screenName = potentialFriends[0]
                def tweets = twitterService.getUserTimeline(screenName)
                def groovyTweets = tweets.findAll { it.statusText =~ pattern }

                //begin following if the user has tweeted about groovy more than x times for the last 200 tweets we got
                if (groovyTweets.size() >= 2)
                {
                    def newFriend = twitterService.follow(screenName)
                    if (newFriend)
                    {
                        mailService.sendAdminMail("[groovytweets] follower scan, adding ${screenName}", "Added qualifying user ${screenName} based on ${groovyTweets.size()} groovy tweets")
                    }
                    else
                    {
                        mailService.sendAdminMail("[groovytweets] follower scan, UNABLE to follow ${screenName}", "Tried to add ${screenName} based on ${groovyTweets.size()} groovy tweets, but failed (already following?)")
                    }
                }

                model.potentialFriends = potentialFriends
                model.potentialFriend = screenName
                model.followerGroovyTweets = groovyTweets.size()
            }
        }
        else
        {
            sendError = true
            mailService.sendAdminMail("[groovytweets] followers public timeline scan FAILED", "memcache does not contain followers list")
        }



        if (sendError)
        response.sendError(503, "Friends/Followers currently not available in memcache...")
        else
        return model
    }

    def topTweets24 = {
        def  tweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 500
                def yesterday = new Date().minus(1)
                tweets = query.resultList.findAll { it.importance > 0 && it.added.after(yesterday)}.sort {a, b -> (a.statusId > b.statusId) ? -1 : 1 }
            } as JpaCallback )

        def tweetStrings = tweets.collect { it.toString() }
        mailService.sendAdminMail("[groovytweets] topTweets24", tweetStrings.join('\n\n'))

        //def cronHeader = request.getHeader('X-AppEngine-Cron')
        //mailService.sendAdminMail("[groovytweets] X-AppEngine-Cron: ${cronHeader}", cronHeader ?: 'no header')


        render(view:'showTweets', model:[tweets:tweets])
    }

    def updateFeeds = {
        log.debug('updateFeeds')
        def latestTweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 50
                latestTweets = query.resultList
            } as JpaCallback )

        memcacheService.put('latestTweets', latestTweets.collect{it.toMap()})
        
        def importantTweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 500
                importantTweets = query.resultList.findAll { it.importance > 0}.sort {a, b -> (a.statusId > b.statusId) ? -1 : 1 }
            } as JpaCallback )

        memcacheService.put('importantTweets', importantTweets.collect{it.toMap()})

        render "done - saved latest tweets (${latestTweets.size()}) and important tweets (${importantTweets.size()}) to memcache for feeds"
    }

    def updateMentions = {
        log.debug 'updateMentions'

        def mentions = twitterService.getMentions()
        def count = 0
        mentions.each {
            status ->
            def m = status.text =~ /@groovytweets suggest @([A-Za-z0-9_]+)/
            if (m)
            {
                def screenName = m[0][1]
                mailService.sendAdminMail("[groovytweets] suggest: @${screenName}", status.text)
                count++
                
            }
        }

        render "done - got ${mentions.size()} new mentions, found ${count} suggestions"
    }

    def updatePopularity = {
        log.debug('updatePopularity')

        def replies = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select reply from org.groovytweets.Reply reply order by reply.replyCount desc")
                query.maxResults = 20
                replies = query.resultList
            } as JpaCallback )

        def base = 'http://chart.apis.google.com/chart?cht=bhs&chs=500x600&chco=FF9900|FF3300&chxt=r,x,t'

        def labels = []
        def data = []
        replies.each { labels << it.screenName; data << it.replyCount}
        
        def max = data[0]
        def axisLabel = ''
        def intervals = (int)(max/10)
        (0..intervals).each { axisLabel += "|${it*10}" }
        axisLabel = axisLabel + "|${max}"
        def label1 = '|1:' + axisLabel
        def label2 = '|2:' + axisLabel

        def chartURL = base + '&chxl=0:|' + labels.reverse().join('|') + label1 + label2 + '&chd=t:' + data.join(',') + "&chds=0,${max}"

        //replace & with &amp;
        chartURL = chartURL.replace('&', '&amp;')
        memcacheService.put('popularityChartURL', chartURL)

        render('done, current popularityChartURL:<br/>' + chartURL)
    }

    def showTweets = {
        log.debug "showTweets"
        def tweets = twitterService.getTweets()
        [tweets:tweets]
    }

    def exists = {
        def statusId = params.id?.toLong() ?: 0l;
        def exists = statusExists(statusId)

        if (exists)
        render "found"
        else
        render "not found"


    }

    def showRateLimitStatus = {
        def stat = twitterService.getRateLimitStatus()
        render("remainingHits ${stat.remainingHits}/ hourlyLimit ${stat.hourlyLimit}/ resetTimeInSeconds ${stat.resetTimeInSeconds}")
    }

    def latest = {
        def  tweetInstanceList =[]
        def total = 0
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by statusId desc limit 1")

                tweetInstanceList = query.resultList
                if (tweetInstanceList && tweetInstanceList.size() > 0)
                total = tweetInstanceList.size()

                if (tweetInstanceList)
                render(view:'/tweet/show', model:[tweetInstance:tweetInstanceList[0]])
                else
                render("could not find latest tweet... hmm?")

            } as JpaCallback )

    }

    /*
    LIKE is not supported, so I simply get the last 200 tweets and try to find the one(s) that contain the search term
     */
    def testStatusFind = {
        def  tweetInstanceList = []
        def total = 0
        def userScreenName = 'graemerocher'
        def search = params.id ?: 'Grails'
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by statusId desc")
                query.maxResults = 200
           
                tweetInstanceList = query.resultList.findAll { tweet -> tweet.statusText.contains(search) && tweet.userScreenName == userScreenName }

                if (tweetInstanceList && tweetInstanceList.size() > 0)
                total = tweetInstanceList.size()

                if (tweetInstanceList)
                render("found tweets that contain '${search}': ${total}")
                else
                render("could not find tweets containing '${search}'")

            } as JpaCallback )

    }

    def testMemcacheGet = {
        def result = memcacheService.get("testId")
        if (result)
        render result
        else
        rener "request returned NULL"
    }

    def testMemcachePut = {
        memcacheService.put("testId", params.id)
        render "saved ${params.id} to memcache"
    }

    def testLastKnownStatusId =
    {
        if (memcacheService.containsKey('lastKnownStatusId'))
        {
            def lastKnownStatusId = (Long)memcacheService.get('lastKnownStatusId')
            render "lastKnownStatusId: ${lastKnownStatusId}"
        }
        else
        render "key lastKnownStatusId does not currently exist in memcache"
    }

    def testMail = {
        try {
            mailService.sendAdminMail("test mail from gae/j", 'this is a test\nsecond line')
            render 'done'
        }
        catch (Exception e)
        {
            render 'could not send email: ' + e.getMessage()
        }
    }


    def statusExists(Long statusId)
    {
        def  tweetInstanceList =[]
        def total = 0
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet where tweet.statusId = :statusId")
                query.setParameter("statusId", statusId)

                tweetInstanceList = query.resultList
                if (tweetInstanceList && tweetInstanceList.size() > 0)
                total = tweetInstanceList.size()

                if (tweetInstanceList)
                return true
                else
                return false

            } as JpaCallback )


    }

    def isRetweet(Tweet tweet)
    {
        //we do not count our own retweets!
        if (tweet.userScreenName == 'groovytweets')
        return null


        def m
        def r = [:]

        m = tweet.statusText =~ /RT:?\s?@([A-Za-z0-9_]+):?\s?(.*)/
        if (m)
        {
            r.screenName = m[0][1]
            r.text = m[0][2]
            println "RT-Match for '${tweet}'"
            println "User: ${m[0][1]}"
            println "Original Text: ${m[0][2]}"
            return r
        }

        //checking for message (via|by @user)
        m = tweet.statusText =~ /(.*)\((via|by) @([A-Za-z0-9]+)\)$/
        if (m)
        {
            r.screenName = m[0][3]
            r.text = m[0][1].trim()
            println "(by/via)-Match for '${tweet}'"
            println "User: ${m[0][3]}"
            println "Original Text: '${m[0][1].trim()}'"
            return r
        }

        m = tweet.statusText =~ /^♺\s?@([A-Za-z0-9_]+):?\s?(.*)/
        if (m)
        {
            r.screenName = m[0][1]
            r.text = m[0][2]
            println "♺-Match for '${tweet}'"
            println "User: ${m[0][1]}"
            println "Original Text: ${m[0][2]}"
            return r
        }

        //checkign for message by|via @username
        m = tweet.statusText =~ /(.*) (by|via) @([A-Za-z0-9]+)$/
        if (m)
        {
            r.screenName = m[0][3]
            r.text = m[0][1]
            println "by/via-Match for '${tweet}'"
            println "User: ${m[0][3]}"
            println "Original Text: '${m[0][1]}'"
            return r
        }

        return null
    }

    def findExistingTweets(statusText, userScreenName) {
        def  tweetInstanceList = []
        def total = 0
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by statusId desc")
                query.maxResults = 200

                tweetInstanceList = query.resultList.findAll { tweet -> tweet.statusText.startsWith(statusText) && tweet.userScreenName == userScreenName }

                return tweetInstanceList ?: []

            } as JpaCallback )

    }

    def logAt(Tweet tweet)
    {
        def m = tweet.statusText =~ /@([A-Za-z0-9_]+)/
        m.each {
            def screenName = it[1]

            //we do not track ourselves :-)
            if (screenName == 'groovytweets')
            return 0

            //lookup screenName in ScreenName entity
            transactionTemplate.execute(
                { status ->


                    jpaTemplate.execute( { entityManager ->
                            def query = entityManager.createQuery("select reply from org.groovytweets.Reply reply where reply.screenName = :screenName")
                            query.setParameter("screenName", screenName)

                            def replies = query.resultList
                            def reply
                            if (replies)
                            {
                                reply = replies[0]
                                reply.replyCount = reply.replyCount + 1
                                jpaTemplate.persist(reply)
                            }
                            else
                            {
                                reply = new Reply()
                                reply.screenName = screenName
                                jpaTemplate.persist(reply)
                                jpaTemplate.flush()
                            }
                        } as JpaCallback )
                } as TransactionCallback )

        }

        return m.size()
    }

    def getReplyMap(groovyTweets)
    {
        def result = [:]
        def screenNamePattern = Pattern.compile(/@([A-Za-z0-9_]+)/)
        groovyTweets.each { tweet ->
            def m = tweet.statusText =~ screenNamePattern
            m.each {
                def screenName = it[1]

                if (screenName == 'groovytweets')
                return

                if (result[screenName])
                result[screenName] = result[screenName] + 1
                else
                result[screenName] = 1
            }
        }
        return result
    }

}
