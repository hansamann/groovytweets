package org.groovytweets



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;

@Entity
class Reply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String screenName
    Integer replyCount = 1

    static constraints = {
    	id(visible:false)
        screenName()
        replyCount()
    }
}
