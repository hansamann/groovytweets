package org.groovytweets

import java.util.regex.Pattern
import com.google.appengine.api.datastore.*
import org.springframework.transaction.support.*
import org.springframework.orm.jpa.*

class CronController
{

    def twitterService
    def memcacheService
    def mailService
    def jpaTemplate
    def transactionTemplate

    //consider word boundaries with \b but let's try first'
    def pattern = Pattern.compile(/groovy|grails|griffon|gr8|gr\*|gorm|gsql|gsp|\bgant|gradle|groosh|builder|plugin|launcher|xmlrpc/, Pattern.CASE_INSENSITIVE)

    def index = { redirect(action:'showTweets', params:params)}

    def updateTweets = {
        log.debug "updateTweets..."
        def tweets = twitterService.getTweets()
        log.info "got ${tweets.size()} tweets..."
        
        

        def groovyTweets = tweets.findAll { it.statusText =~ pattern }

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
                else if (!tweet.userScreenName == 'groovytweets') //exclude our RT's
                {
                    transactionTemplate.execute(
                    { status ->
                        jpaTemplate.persist(tweet)
                        jpaTemplate.flush()
                        added++
                    } as TransactionCallback )
                }
            }
        }

        render "done - added ${added} tweets, logged @replies: ${replyCount}"
    }


    def updateFriends = {
        log.debug "updateFriends"
        def friends = twitterService.getAllFriends() //intializes page to 1

        def cacheFriends = friends.collect { [id:it.id, name:it.name, screenName:it.screenName, profileImageURL:it.profileImageURL]}
        
        memcacheService.put("friends", cacheFriends)
        render "done - saved friends list to memcache, size: ${cacheFriends.size()}"
    }

    def scanRandomUserTimelines = {
        log.debug "scanUserTimeline"
        if (memcacheService.containsKey('friends'))
        {
            def friends = memcacheService.get('friends')
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

            [replies:replyMap, user:user]
           
        }
        else
            response.sendError(503, "Friends currently not available in memcache...")
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

        def cronHeader = request.getHeader('X-AppEngine-Cron')
        mailService.sendAdminMail("[groovytweets] X-AppEngine-Cron: ${cronHeader}", cronHeader ?: 'no header')


        render(view:'showTweets', model:[tweets:tweets])
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

        m = tweet.statusText =~ /RT:? @([A-Za-z0-9_]+):?\s{1,}(.*)/
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

        m = tweet.statusText =~ /^♺ @([A-Za-z0-9_]+):?\s{1,}(.*)/
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
