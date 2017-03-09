package io.specto.hoverfly.junit.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HoverflyInfo {

    private final String destination;
    private final String mode;
    private final Usage usage;
    private final Middleware middleware;

    @JsonCreator
    public HoverflyInfo(@JsonProperty("destination") String destination,
                        @JsonProperty("mode") String mode,
                        @JsonProperty("usage") Usage usage,
                        @JsonProperty("middleware") Middleware middleware) {
        this.destination = destination;
        this.mode = mode;
        this.usage = usage;
        this.middleware = middleware;
    }


    public String getDestination() {
        return destination;
    }

    public String getMode() {
        return mode;
    }

    public Usage getUsage() {
        return usage;
    }

    public Middleware getMiddleware() {
        return middleware;
    }

    private static class Usage {

        private Counters counters;

        public Usage() {
        }

        public Counters getCounters() {
            return counters;
        }

        public void setCounters(Counters counters) {
            this.counters = counters;
        }
    }

    private static class Middleware {
        private String binary;
        private String script;
        private String remote;

        public String getBinary() {
            return binary;
        }

        public void setBinary(String binary) {
            this.binary = binary;
        }

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }

        public String getRemote() {
            return remote;
        }

        public void setRemote(String remote) {
            this.remote = remote;
        }
    }

    private static class Counters {
        private int capture;
        private int modify;
        private int simulate;
        private int synthesize;

        public Counters() {
        }

        public int getCapture() {
            return capture;
        }

        public void setCapture(int capture) {
            this.capture = capture;
        }

        public int getModify() {
            return modify;
        }

        public void setModify(int modify) {
            this.modify = modify;
        }

        public int getSimulate() {
            return simulate;
        }

        public void setSimulate(int simulate) {
            this.simulate = simulate;
        }

        public int getSynthesize() {
            return synthesize;
        }

        public void setSynthesize(int synthesize) {
            this.synthesize = synthesize;
        }
    }
}
