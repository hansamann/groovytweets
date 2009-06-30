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

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class FeedController {

    def memcacheService

    def latest =
    {
       def tweets = memcacheService.get('latestTweets') ?: []

        render(feedType:"rss", feedVersion:"2.0") {
            title = "groovytweets ::: latest tweets"
            link = "http://www.groovytweets.org"
            description = "The latest tweets from the Groovy community, updated every 15 minutes."

            tweets.each() { tweet ->
                entry(tweet.statusText) {
                    title = "${tweet.userScreenName} (${tweet.userRealName}): ${tweet.statusText}"
                    link = "http://twitter.com/${tweet.userScreenName}/statuses/${tweet.statusId}"
                    publishedDate = tweet.added
                    author = tweet.userRealName
                    content() {
                        type='text/html'
                        return tweet.encodedStatusText
                    }
                }
            }
        }

    }

    def important =
    {
        def tweets = memcacheService.get('importantTweets') ?: []

        render(feedType:"rss", feedVersion:"2.0") {
            title = "groovytweets ::: important tweets"
            link = "http://www.groovytweets.org"
            description = "The important tweets from the Groovy community, updated every 15 minutes."

            tweets.each() { tweet ->
                entry(tweet.statusText) {
                    title = "${tweet.statusText}"
                    link = "http://twitter.com/${tweet.userScreenName}/statuses/${tweet.statusId}"
                    publishedDate = tweet.added
                    author = tweet.userRealName
                    content() {
                        type='text/html'
                        return tweet.encodedStatusText
                    }
                }
            }
        }

    }
}
