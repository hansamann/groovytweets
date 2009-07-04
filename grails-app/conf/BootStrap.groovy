import grails.util.GrailsUtil
import org.groovytweets.*

class BootStrap {

     def init = { servletContext ->
        if (GrailsUtil.isDevelopmentEnv())
        {
           //cannot use GORM, argh!
        }

     }
     def destroy = {
     }
} 