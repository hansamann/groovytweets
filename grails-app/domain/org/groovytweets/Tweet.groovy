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

import javax.persistence.*;
import com.ocpsoft.pretty.time.PrettyTime;

// import com.google.appengine.api.datastore.Key;

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
@Entity
class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    Date added = new Date()

    Long statusId
    String statusText

    String userRealName
    String userScreenName
    String userImage
    Long userId

    Integer importance = 0

    static constraints = {
    	id(visible:false)
        added(nullable:false)
        statusId()
        statusText()
        userRealName()
        userScreenName()
        userImage()
        userId()
        importance()
    }

    def String toString()
    {
        return "User: ${userScreenName}, ${added}, Status: ${statusId}, Importance: ${importance}, Text: ${statusText}"
    }

    def toMap()
    {
        def m = [:]
        m.id = id
        m.added = added
        m.statusId = statusId
        m.statusText = statusText
        m.userRealName = userRealName
        m.userScreenName = userScreenName
        m.userImage = userImage
        m.userId = userId
        m.importance = importance
        m.encodedStatusText = getEncodedStatusText()
        return m
    }

    def hasRealName()
    {
        userRealName != userScreenName
    }

    def getEncodedStatusText()
    {
        def htmlEncoded = statusText.encodeAsHTML()
        def m = htmlEncoded =~ /(https?:\/\/[^ ]+)/
        m.each {
        //println it[1]
            def url = it[1]
            htmlEncoded = htmlEncoded.replace(url, /<a target="_blank" href="${url}">${url}<\/a>/)
        }

        m = htmlEncoded =~ /@([A-Za-z0-9_]+)/
        m.each {
            def screenName = it[1]
            htmlEncoded = htmlEncoded.replace("@${screenName}", /<a target="_blank" class="screenNameLink" href="http:\/\/twitter.com\/${screenName}">@${screenName}<\/a>/)
        }

        m = htmlEncoded =~ /#([A-Za-z0-9_]+)/
        m.each {
            def hashTag = it[1]
            htmlEncoded = htmlEncoded.replace("#${hashTag}", /<a target="_blank" class="screenNameLink" href="http:\/\/search.twitter.com\/search?q=%23${hashTag}">#${hashTag}<\/a>/)
        }
       
        //println "final result: ${statusText}"
        return htmlEncoded
    }

    def getRetweetURL()
    {
        //http://twitter.com/home/?status=RT+%40TechCrunch+Google+App+Engine+Broken+For+4+Hours+And+Counting+http%3A%2F%2Fcli.gs%2FQNj8Y+%28via+%40tweetmeme%29
        return 'http://twitter.com/home/?status=' + "RT @${userScreenName}: ${statusText}".encodeAsURL()
    }

    def getPrettyAdded()
    {
        return new PrettyTime().format(added)
    }
}
