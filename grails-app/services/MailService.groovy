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

import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class MailService {

    boolean transactional = true

    def sendAdminMail(msgSubject, msgBody)
    {        
        Session session = Session.getDefaultInstance(new Properties(), null)
        try
        {
            Message msg = new MimeMessage(session)
            msg.setFrom(new InternetAddress("sven.haiges@googlemail.com"))
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("hansamann@yahoo.de", "Sven Haiges"))
            msg.setSubject(msgSubject ?: '[groovytweets]')
            msg.setText(msgBody ?: '')
            Transport.send(msg)
        }
        catch (Exception e)
        {
            log.error ("Unable to send email", e)
            return false
        }

        return true
    }
}
