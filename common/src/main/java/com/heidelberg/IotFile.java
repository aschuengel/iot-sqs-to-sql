package com.heidelberg;

public class IotFile {
    private IotMessage message;
    private byte[] body;

    public IotMessage getMessage() {
        return message;
    }

    public void setMessage(IotMessage message) {
        this.message = message;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
