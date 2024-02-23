package it.polimi.ingsw.Server.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.GsonOptionalSerializer;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.Server.Model.Shelf.SHELF_DIM_X;
import static it.polimi.ingsw.Server.Model.Shelf.SHELF_DIM_Y;

/**
 * This class represents the Personal Goal cards, thanks to which players may grant points
 * if they match the pattern with the corresponding ObjectCards
 */
public class PersonalGoalCard extends UnicastRemoteObject implements PersonalGoalCardInterface {
    /**
     * Limit of Personal Goal Cards.
     */
    public static final int LIMIT = 12;
    /**
     * This attribute stands for the id of the Personal Goal Card, which permits to
     * identify the different Personal Goal Cards.
     */
    @Expose
    private final int type;
    /**
     * The pattern represents the position of specific types of object cards.
     */
    @Expose
    private Optional<ObjectCardType>[][] pattern;

    /**
     * Class constructor. The pattern is created by reading information from the file.
     *
     * @param type personal goal cards' id
     * @throws Exception if the file is not read properly
     */

    public PersonalGoalCard(int type) throws Exception {
        this.type = type;

        this.pattern = new Optional[SHELF_DIM_X][SHELF_DIM_Y];

        declarePattern(type);
    }

    /**
     * Related to Game's refresh strategy
     */
    private PersonalGoalCard(int type, Optional<ObjectCardType>[][] pattern) throws RemoteException {
        this.type = type;
        this.pattern = pattern;
    }

    private void declarePattern(int type) throws URISyntaxException, IOException {
        // JSON Parser
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .create();

        // resource locator
        URI uri = ClassLoader.getSystemResource("model/pgc/"
                .concat(String.valueOf(type))
                .concat(".json")).toURI();

        // FileSystem support structure, related to JAR file structure
        FileSystem fs = null;
        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        fs = provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // creates a temporary File System for artifacts file scheme
                        fs = provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        // create pattern
        String res;
        try (Stream<String> stream = Files.lines(Paths.get(uri), StandardCharsets.UTF_8)) {
            res = stream.map(Object::toString)
                    .collect(Collectors.joining());
        }

        // close FileSystem
        if (fs != null) fs.close();

        ObjectCardType[][] tempPattern = gson.fromJson(res, ObjectCardType[][].class);

        for (int i = 0; i < SHELF_DIM_X; i++) {
            for (int j = 0; j < SHELF_DIM_Y; j++) {
                if (tempPattern[i][j] == null)
                    pattern[i][j] = Optional.empty();
                else pattern[i][j] = Optional.of(tempPattern[i][j]);
            }
        }
    }

    /**
     * Getter method for Personal Goal Card
     *
     * @return type of the card
     */
    public int getType() {
        return type;
    }

    /**
     * returns the pattern
     *
     * @return a multidimensional array of optional object card types
     */
    public Optional<ObjectCardType>[][] getPattern() {
        return pattern;
    }

    /**
     * getter method
     *
     * @param x coordinate
     * @param y coordinate
     * @return ObjectCardType in position[x][y] of the PersonalCard
     */

    public ObjectCardType getPlainType(int x, int y) {
        return pattern[x][y].orElse(null);
    }

    /**
     * getter method
     *
     * @param x coordinate
     * @param y coordinate
     * @return ordinal of ObjectCardType or -1 if the card is empty
     * @throws Exception if coordinates are invalid
     */
    public int getOrdinal(int x, int y) throws Exception {
        if (x >= 0 && x <= SHELF_DIM_X - 1 && y >= 0 && y <= SHELF_DIM_Y - 1)
            if (pattern[x][y].isPresent())
                return pattern[x][y].get().ordinal();
            else return -1;
        else throw new Exception();
    }

    /**
     * Refreshed copy, after server reload from file
     *
     * @return new copy of the object
     * @throws RemoteException related to RMI
     */
    public PersonalGoalCard getCopy() throws RemoteException {
        return new PersonalGoalCard(this.type, this.pattern);
    }
}
