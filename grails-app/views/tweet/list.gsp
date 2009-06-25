<%@ page import="org.groovytweets.Tweet" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Tweet List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Tweet</g:link></span>
        </div>
        <div class="body">
            <h1>Tweet List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="added" title="Added" />
                        
                   	        <g:sortableColumn property="statusId" title="Status Id" />
                        
                   	        <g:sortableColumn property="statusText" title="Status Text" />
                        
                   	        <g:sortableColumn property="userRealName" title="User Real Name" />
                        
                   	        <g:sortableColumn property="userScreenName" title="User Screen Name" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${tweetInstanceList}" status="i" var="tweetInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${tweetInstance.id}">${fieldValue(bean:tweetInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:tweetInstance, field:'added')}</td>
                        
                            <td>${fieldValue(bean:tweetInstance, field:'statusId')}</td>
                        
                            <td>${fieldValue(bean:tweetInstance, field:'statusText')}</td>
                        
                            <td>${fieldValue(bean:tweetInstance, field:'userRealName')}</td>
                        
                            <td>${fieldValue(bean:tweetInstance, field:'userScreenName')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${tweetInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
