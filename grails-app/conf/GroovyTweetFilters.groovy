import com.google.appengine.api.users.*

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class GroovyTweetFilters {
    def filters = {
        securityFilter(controller:'*', action:'*'){
            before = {
                if (controllerName == 'public' || controllerName == 'feed')
                    return true

                def cronHeader = request.getHeader('X-AppEngine-Cron')
                if (controllerName == 'cron' && cronHeader == 'true')
                    return true

                def userService = UserServiceFactory.userService

                if (!userService.currentUser || !userService.isUserAdmin()) {
                    def loginURL = userService.createLoginURL(request.requestURI)
                    redirect(url:loginURL)
                    return false
                }
                else
                {
                    def logoutURL = userService.createLogoutURL('http://groovytweets.appspot.com/')
                    request.setAttribute('logoutURL', logoutURL)
                    return true
                }

            }
        }
    }
}

