package it.polimi.ingsw.Server.Controller;

import com.google.gson.*;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.Shelf;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Gson Move Deserializer
 */
public class GsonMoveDeserializer implements JsonDeserializer<Move> {
    /**
     * Game's Board
     */
    private final Board b;
    /**
     * Player's Shelf
     */
    private final Shelf s;

    /**
     * MoveDeserializer Constructor
     *
     * @param b Game's Board
     * @param s Player's Shelf
     */
    public GsonMoveDeserializer(Board b, Shelf s) {
        this.b = b;
        this.s = s;
    }

    /**
     * Deserialization strategy
     */
    @Override
    public Move deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Move m = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create()
                .fromJson(json.getAsJsonObject(), Move.class);
        m.setBoardAndShelf(b, s);
        return m;
    }
}
