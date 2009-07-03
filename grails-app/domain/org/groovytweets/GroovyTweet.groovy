package org.groovytweets



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;

@Entity
class GroovyTweet implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

    Date added
    Integer importance
    Long statusId
    String statusText
    Long userId
    String userRealName
    String userScreenName

    static constraints = {
    	id visible:false
	}
}
