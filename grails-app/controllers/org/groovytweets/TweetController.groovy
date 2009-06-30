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
class TweetController {

    def jpaTemplate
    def transactionTemplate
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

	def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
		def  tweetInstanceList =[]
		def total = 0
		jpaTemplate.execute( { entityManager ->
			def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
			query.maxResults = params.max
			query.firstResult = params?.offset?.toInteger() ?: 0
			 tweetInstanceList = query.resultList
			if(   tweetInstanceList &&   tweetInstanceList.size() > 0){
				total =   tweetInstanceList.size()
			}			
			
		} as JpaCallback )
		
		[   tweetInstanceList :   tweetInstanceList,   tweetInstanceTotal: total ]
    }

	def list50 = {
		def  tweetInstanceList =[]
		def total = 0
		jpaTemplate.execute( { entityManager ->
                    //where tweet.added > :yesterday
                    def query = entityManager.createQuery("select tweet from org.groovytweets.Tweet tweet order by tweet.statusId desc")
			query.maxResults = 50
			tweetInstanceList = query.resultList
			if(   tweetInstanceList &&   tweetInstanceList.size() > 0){
				total =   tweetInstanceList.size()
			}

		} as JpaCallback )

		[   tweetInstanceList :   tweetInstanceList,   tweetInstanceTotal: total ]
    }


    def show = {
	    def tweetInstance = jpaTemplate.find( Tweet.class, Long.parseLong( params.id )  )
        if(!tweetInstance) {
            flash.message = "Tweet not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ tweetInstance : tweetInstance ] }
    }

    def delete = {
		transactionTemplate.execute( {
		    def tweetInstance = jpaTemplate.find( Tweet.class, Long.parseLong( params.id )  )
	        if(tweetInstance) {
	            try {
	                jpaTemplate.remove(tweetInstance)
					jpaTemplate.flush()
	                flash.message = "Tweet ${params.id} deleted"
	                redirect(action:list)
	            }
	            catch(Exception e) {
	                flash.message = "Tweet ${params.id} could not be deleted"
	                redirect(action:show,id:params.id)
	            }
	        }
	        else {
	            flash.message = "Tweet not found with id ${params.id}"
	            redirect(action:list)
	        }
			
		} as TransactionCallback )
    }

    def edit = {
	    def tweetInstance = jpaTemplate.find( Tweet.class, Long.parseLong( params.id )  )
		if(!tweetInstance) {
            flash.message = "Tweet not found with id ${params.id}"
            redirect(action:list)
        }
        else {
        	return [ tweetInstance : tweetInstance ]
        }
    }

    def update = {
		transactionTemplate.execute( { status ->
		 	def tweetInstance = jpaTemplate.find( Tweet.class, Long.parseLong( params.id )  )

	    	if(tweetInstance) {
	            tweetInstance.properties = params
	            if(!tweetInstance.hasErrors() && tweetInstance.validate()){

					try{
						jpaTemplate.persist(tweetInstance)
					} catch( Exception e ){
					   	render(view:'edit',model:[tweetInstance:tweetInstance])
					}finally{
						flash.message = "Tweet ${params.id} updated"
		                redirect(action:show,id:tweetInstance.id)
					}        
	 			}
	            else {
					status.setRollbackOnly()
	                render(view:'edit',model:[tweetInstance:tweetInstance])
	            }
	        }
	        else {
	            flash.message = "Tweet not found with id ${params.id}"
	            redirect(action:list)
	        }
			
		} as TransactionCallback )
	
    }

    def create = {
        def tweetInstance = new Tweet()
        tweetInstance.properties = params
        return ['tweetInstance':tweetInstance]
    }

    def save = {
		transactionTemplate.execute( { status ->	
	        def tweetInstance = new Tweet(params)
			if(!tweetInstance.hasErrors() && tweetInstance.validate() ) {
				jpaTemplate.persist(tweetInstance)
				jpaTemplate.flush()
				flash.message = "Tweet ${tweetInstance.id} created"
				redirect(action:show,id:tweetInstance.id)	
			}
			else {
				status.setRollbackOnly()				
				render(view:'create',model:[tweetInstance:tweetInstance])				
			}

		} as TransactionCallback )
	
        
    }
}
