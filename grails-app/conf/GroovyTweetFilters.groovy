import com.google.appengine.api.users.*

/**
 *
 * @author hansa
 */
class GroovyTweetFilters {
    def filters = {
        securityFilter(controller:'*', action:'*'){
            before = {
                if (controllerName == 'public')
                    return true

                if (controllerName == 'cron')
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

