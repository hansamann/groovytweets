package org.groovytweets



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;

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
}
