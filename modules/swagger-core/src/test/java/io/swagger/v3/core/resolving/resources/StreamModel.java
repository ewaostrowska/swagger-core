package io.swagger.v3.core.resolving.resources;

import io.swagger.v3.oas.annotations.media.ArraySchema;

public class StreamModel {
    
    public static class Greeting {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    @ArraySchema
    private java.util.stream.Stream<Greeting> greetings;

    @ArraySchema
    public java.util.stream.Stream<Greeting> getGreetings() {
        return greetings;
    }

    public void setGreetings(java.util.stream.Stream<Greeting> greetings) {
        this.greetings = greetings;
    }
}
