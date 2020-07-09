package com.heidelberg;

import java.util.Date;

public class FakeMessageGenerator {
    public IotMessage generate() {
        IotMessage message = new IotMessage();
        message.setDate(new Date());
        message.setComment("Created at " + message.getDate());
        message.setType("Alert");
        return message;
    }
}
