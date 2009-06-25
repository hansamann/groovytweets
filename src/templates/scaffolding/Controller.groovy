<%=packageName ? "package ${packageName}\n\n" : ''%>
import com.google.appengine.api.datastore.*
import org.springframework.transaction.support.*
import org.springframework.orm.jpa.*
class ${className}Controller {

	def jpaTemplate
	def transactionTemplate
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

	def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
		def  ${propertyName}List =[]
		def total = 0
		jpaTemplate.execute( { entityManager ->
			def query = entityManager.createQuery("select from ${packageName}.${className}")
			query.maxResults = params.max
			query.firstResult = params?.offset?.toInteger() ?: 0
			 ${propertyName}List = query.resultList
			if(   ${propertyName}List &&   ${propertyName}List.size() > 0){
				total =   ${propertyName}List.size()
			}			
			
		} as JpaCallback )
		
		[   ${propertyName}List :   ${propertyName}List,   ${propertyName}Total: total ]
    }


    def show = {
	    def ${propertyName} = jpaTemplate.find( ${className}.class, Long.parseLong( params.id )  )
        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else { return [ ${propertyName} : ${propertyName} ] }
    }

    def delete = {
		transactionTemplate.execute( {
		    def ${propertyName} = jpaTemplate.find( ${className}.class, Long.parseLong( params.id )  )
	        if(${propertyName}) {
	            try {
	                jpaTemplate.remove(${propertyName})
					jpaTemplate.flush()
	                flash.message = "${className} \${params.id} deleted"
	                redirect(action:list)
	            }
	            catch(Exception e) {
	                flash.message = "${className} \${params.id} could not be deleted"
	                redirect(action:show,id:params.id)
	            }
	        }
	        else {
	            flash.message = "${className} not found with id \${params.id}"
	            redirect(action:list)
	        }
			
		} as TransactionCallback )
    }

    def edit = {
	    def ${propertyName} = jpaTemplate.find( ${className}.class, Long.parseLong( params.id )  )
		if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
        	return [ ${propertyName} : ${propertyName} ]
        }
    }

    def update = {
		transactionTemplate.execute( { status ->
		 	def ${propertyName} = jpaTemplate.find( ${className}.class, Long.parseLong( params.id )  )

	    	if(${propertyName}) {
	            ${propertyName}.properties = params
	            if(!${propertyName}.hasErrors() && ${propertyName}.validate()){

					try{
						jpaTemplate.persist(${propertyName})
					} catch( Exception e ){
					   	render(view:'edit',model:[${propertyName}:${propertyName}])
					}finally{
						flash.message = "${className} \${params.id} updated"
		                redirect(action:show,id:${propertyName}.id)
					}        
	 			}
	            else {
					status.setRollbackOnly()
	                render(view:'edit',model:[${propertyName}:${propertyName}])
	            }
	        }
	        else {
	            flash.message = "${className} not found with id \${params.id}"
	            redirect(action:list)
	        }
			
		} as TransactionCallback )
	
    }

    def create = {
        def ${propertyName} = new ${className}()
        ${propertyName}.properties = params
        return ['${propertyName}':${propertyName}]
    }

    def save = {
		transactionTemplate.execute( { status ->	
	        def ${propertyName} = new ${className}(params)
			if(!${propertyName}.hasErrors() && ${propertyName}.validate() ) {
				jpaTemplate.persist(${propertyName})
				jpaTemplate.flush()
				flash.message = "${className} \${${propertyName}.id} created"
				redirect(action:show,id:${propertyName}.id)	
			}
			else {
				status.setRollbackOnly()				
				render(view:'create',model:[${propertyName}:${propertyName}])				
			}

		} as TransactionCallback )
	
        
    }
}
