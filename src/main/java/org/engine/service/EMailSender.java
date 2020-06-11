package org.engine.service;

import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EMailSender {

    private static final Logger LOG = LoggerFactory.getLogger(EMailSender.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.sending-enabled}")
    private boolean sendingEnabled;
    
    @Autowired
    private Configuration freemarkerConfig;

    public void sendMail(String to, String subject, EmailModel content) {
        validateSendingParams(to, subject, content);

        LOG.debug(String.format("Sending email to %s, with subject %s and content %s", to, subject, content));

        if (!sendingEnabled) {
            LOG.debug("Sending functionality is disabled, skipping...");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
                        
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
                                    
            String data = geFreeMarkerTemplateContent(content.getModel());

            helper.setFrom("plamen.terziev@sunlex.biz");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(data, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            LOG.error("Failed to send email");
            ex.printStackTrace();
		}
    }

    private void validateSendingParams(String to, String subject, EmailModel content) {
        if (to == null || to.isEmpty() ||
                subject == null || subject.isEmpty() ||
                content == null) {
            LOG.error("missing email parameter");
            throw new IllegalArgumentException();
        }
    }
	
	private String geFreeMarkerTemplateContent(Map<String, Object> model){
        StringBuffer content = new StringBuffer();
        try{
         content.append(FreeMarkerTemplateUtils.processTemplateIntoString( 
        		 freemarkerConfig.getTemplate("emails_activate.html"), model));
         return content.toString();
        }catch(Exception e){
            System.out.println("Exception occurred while processing fmtemplate:"+e.getMessage());
        }
          return "";
    }
}
