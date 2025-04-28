package com.esprit.Services;

import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppService {
    // Twilio credentials
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public WhatsAppService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendWhatsAppMessage(String toPhoneNumber, String messageContent) {
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + toPhoneNumber), // Destination
                new PhoneNumber("whatsapp:+14155238886"),     // Twilio Sandbox WhatsApp Number
                messageContent
        ).create();

        System.out.println("Message sent! SID: " + message.getSid());
    }
}