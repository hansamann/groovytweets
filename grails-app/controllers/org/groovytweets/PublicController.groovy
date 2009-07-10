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

import com.google.appengine.api.datastore.*
import org.springframework.transaction.support.*
import org.springframework.orm.jpa.*
import grails.converters.JSON

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
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
        def tweetInstanceList = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = 500
                tweetInstanceList = query.resultList.findAll { it.importance > 0}.sort {a, b -> (a.statusId > b.statusId) ? -1 : 1 }

            } as JpaCallback )

        [tweets:tweetInstanceList]
    }

    def pullTweets = {
        //unknown bug, maybe some browsers have problems with the list50.gsp page/javascript
        if (!params.id || params.id == 'undefined')
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


    def pullMeta = {
        jpaTemplate.execute( { entityManager ->
                if (!params.id || params.id == 'undefined')
                    return

                def tweetCount = params.id.toLong()

                if (tweetCount > 150)
                    tweetCount = 150

                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
                query.maxResults = tweetCount
                def meta = query.resultList.collect {tweet -> [tweet.statusId, tweet.importance, tweet.prettyAdded]}
                render meta as JSON
            } as JpaCallback )
    }

    def pullInfo = {
        if (memcacheService.containsKey('friends') && params.id)
        {
            if (params.id == 'groovytweets')
            {
                def self = memcacheService.get('self')
                if (self)
                    render self as JSON
                else
                    render "-1"
            }
            else
            {
                def friends = memcacheService.get('friends')
                def friend = friends.find { it.screenName == params.id }
                if (friend)
                    render friend as JSON
                else
                    render "-1"
                }
        }
        else
            render "-1";
    }

    def friends = {
        if (memcacheService.containsKey('friends'))
            [friends:memcacheService.get('friends')]
        else
            [friends:[]]
    }

    def about = {}
    
    def popular = {
        if (memcacheService.containsKey('popularityChartURL'))
            [popularityChartURL:memcacheService.get('popularityChartURL')]
        else
            [popularityChartURL:null]
    }
}