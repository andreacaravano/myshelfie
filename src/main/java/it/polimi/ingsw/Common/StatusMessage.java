package it.polimi.ingsw.Common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Status Messages implementation, informative of game's evolution
 */
public class StatusMessage {
    @Expose
    Status status;
    @Expose
    String message;

    /**
     * Represents a Status Message, informative of game's evolution
     *
     * @param status  in enumeration
     * @param message attachment
     */
    public StatusMessage(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * @param status in enumeration
     */
    public StatusMessage(Status status) {
        this.status = status;
    }

    /**
     * JSON Status Message Syntax validator
     *
     * @param line read from the client
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String line) {
        try {
            if (line != null) {
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();

                gson.fromJson(line, StatusMessage.class);
                return true;
            } else return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * @return type of Status Message
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return message attachment
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusMessage that = (StatusMessage) o;
        return status == that.status;
    }
}