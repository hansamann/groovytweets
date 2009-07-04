<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create Tweet</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Tweet List</g:link></span>
        </div>
        <div class="body">
            <h1>Create Tweet</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${tweetInstance}">
            <div class="errors">
                <g:renderErrors bean="${tweetInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="added">Added:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'added','errors')}">
                                    <g:datePicker name="added" value="${tweetInstance?.added}" precision="minute" ></g:datePicker>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="statusId">Status Id:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'statusId','errors')}">
                                    <input type="text" id="statusId" name="statusId" value="${fieldValue(bean:tweetInstance,field:'statusId')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="statusText">Status Text:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'statusText','errors')}">
                                    <input type="text" id="statusText" name="statusText" value="${fieldValue(bean:tweetInstance,field:'statusText')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="userRealName">User Real Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'userRealName','errors')}">
                                    <input type="text" id="userRealName" name="userRealName" value="${fieldValue(bean:tweetInstance,field:'userRealName')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="userScreenName">User Screen Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'userScreenName','errors')}">
                                    <input type="text" id="userScreenName" name="userScreenName" value="${fieldValue(bean:tweetInstance,field:'userScreenName')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="userId">User Id:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'userId','errors')}">
                                    <input type="text" id="userId" name="userId" value="${fieldValue(bean:tweetInstance,field:'userId')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="importance">Importance:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'importance','errors')}">
                                    <input type="text" id="importance" name="importance" value="${fieldValue(bean:tweetInstance,field:'importance')}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="importance">User Image:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tweetInstance,field:'userImage','errors')}">
                                    <input type="text" id="userImage" name="userImage" value="${fieldValue(bean:tweetInstance,field:'userImage')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
