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

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class PlayController {

    def jpaTemplate
    def transactionTemplate

    def index = { }

    def query = {
        def  tweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery(params.jpaquery)
                query.maxResults = 500
                tweets = query.resultList
            } as JpaCallback )

        render(view:'query', model:[tweets:tweets])
    }

    def query1 = {
        def  tweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet where tweet.importance > 1 order by tweet.importance desc")
                query.maxResults = 500
                tweets = query.resultList
            } as JpaCallback )

        render(view:'query', model:[tweets:tweets])
    }

    //find out importannt tweets of a user?
    def query2 = {
        def  tweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet where tweet.userScreenName = 'hansamann' order by tweet.importance desc")
                query.maxResults = 500
                tweets = query.resultList
            } as JpaCallback )

        render(view:'query', model:[tweets:tweets])
    }
    
    def query3 = {
        def  tweets = []
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet where tweet.added > ?1 order by tweet.added desc")
                //TODO then sort by statusId programmatically
                //>yesterday does not seem to work on appengine
                query.setParameter(1, new Date().minus(1))
                query.maxResults = 500
                tweets = query.resultList
            } as JpaCallback )

        render(view:'query', model:[tweets:tweets])
    }

    def deleteGroovyTweets = {
        jpaTemplate.execute( { entityManager ->
                def query = entityManager.createQuery("DELETE FROM org.groovytweets.GroovyTweet oldtweet")
                query.executeUpdate()
            } as JpaCallback )

        render("Deleted entities")
    }
}
