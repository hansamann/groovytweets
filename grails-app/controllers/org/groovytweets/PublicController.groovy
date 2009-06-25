package org.groovytweets

import com.google.appengine.api.datastore.*
import org.springframework.transaction.support.*
import org.springframework.orm.jpa.*
import grails.converters.JSON


class PublicController {
    def jpaTemplate
    def transactionTemplate
    def memcacheService

    def index = { redirect(action:'list50', params:params) }

    def list50 = {
        def  tweetInstanceList =[]
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 50
                tweetInstanceList = query.resultList
            } as JpaCallback )

        [tweets:tweetInstanceList]
    
    }

    def listImportant = {
        def  tweetInstanceList = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 500
                tweetInstanceList = query.resultList.findAll { it.importance > 0}.sort {a, b -> (a.statusId > b.statusId) ? -1 : 1 }

            } as JpaCallback )

        [tweets:tweetInstanceList]
    }

    def pullTweets = {
        //unknown bug, maybe some browsers have problems with the list50.gsp page/javascript
        if (params.id == 'undefined')
            return

        def latest = params.id.toLong()

        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 10
                def tweets = query.resultList.findAll { it.statusId > latest }

                if (tweets)
                {
                    response.setHeader("latestStatusId", tweets[0].statusId.toString())
                    render(template:'tweets', model:[tweets:tweets])
                }
                else
                {
                    render "-1"
                }


            } as JpaCallback )
    }


    def pullRelevances = {
        jpaTemplate.execute( { entityManager ->
                def tweetCount = params.id.toLong()
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = tweetCount
                def statusRelevanceMap = query.resultList.collect {tweet -> [tweet.statusId, tweet.importance]}
                render statusRelevanceMap as JSON
            } as JpaCallback )
    }

    def friends = {
        if (memcacheService.containsKey('friends'))
            [friends:memcacheService.get('friends')]
        else
            [friends:[]]
    }

    def about = {}


}
