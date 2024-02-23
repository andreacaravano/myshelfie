package it.polimi.ingsw.Server.Model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Client.ClientModel.ClientBoard;
import it.polimi.ingsw.Client.ClientModel.ClientMove;
import it.polimi.ingsw.Client.ClientModel.ClientPersonalGoalCard;
import it.polimi.ingsw.Client.ClientModel.ClientShelf;
import it.polimi.ingsw.Client.View.BoardView;
import it.polimi.ingsw.Client.View.PersonalGoalCardView;
import it.polimi.ingsw.Client.View.ShelfView;
import it.polimi.ingsw.Common.*;
import it.polimi.ingsw.Server.Controller.GsonMoveDeserializer;
import it.polimi.ingsw.Server.Controller.Move;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.Common.GsonInterfaceSerializer.interfaceSerializer;

/**
 * General JSON serialization tests
 */
public class SerializationTest {
    @Test
    void boardSerialization() throws Exception {
        Board b = new Board(4, new ArrayList<>());
        Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(b);
        System.out.println(res);
        BoardView.print(b, new PrintWriter(System.out));
        b = gson.fromJson(res, Board.class);
        BoardView.print(b, new PrintWriter(System.out));

        ClientBoard cb = gson.fromJson(res, ClientBoard.class);
        BoardView.print(cb, new PrintWriter(System.out));
    }

    @Disabled
    void boardSpacesSerialization() throws Exception {
        ExclusionStrategy strategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                // return fieldAttributes.getName().equals("card");
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };

        // Gson Builder
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(strategy)
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .setPrettyPrinting()
                .create();

        for (int i = 2; i <= 4; i++) {
            Board b = new Board(i, null);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
                    .concat("/res/model/board/")
                    .concat(String.valueOf(i))
                    .concat(".json")))) {

                bw.write(gson.toJson(b.getSpaces()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // resource locator
            URI uri = ClassLoader.getSystemResource("model/board/"
                    .concat(String.valueOf(i))
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

            String res;
            try (Stream<String> stream = Files.lines(Paths.get(uri), StandardCharsets.UTF_8)) {
                res = stream.map(Object::toString)
                        .collect(Collectors.joining());
            }

            // close FileSystem
            if (fs != null) fs.close();

            System.out.println(res);
        }
    }

    @Test
    void shelfSerialization() throws Exception {
        Shelf s = new Shelf();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
            }
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(s);
        System.out.println(res);
        ShelfView.print(s, new PrintWriter(System.out));
        s = gson.fromJson(res, Shelf.class);
        ShelfView.print(s, new PrintWriter(System.out));

        ClientShelf cl = gson.fromJson(res, ClientShelf.class);
        ShelfView.print(cl, new PrintWriter(System.out));
    }

    @Test
    void personalGoalCardSerialization() throws Exception {
        PersonalGoalCard pgc = new PersonalGoalCard(1);
        Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(pgc);
        System.out.println(res);
        PersonalGoalCardView.print(pgc, new PrintWriter(System.out));
        pgc = gson.fromJson(res, PersonalGoalCard.class);
        PersonalGoalCardView.print(pgc, new PrintWriter(System.out));

        ClientPersonalGoalCard cpgc = gson.fromJson(res, ClientPersonalGoalCard.class);
        PersonalGoalCardView.print(cpgc, new PrintWriter(System.out));
    }

    @Disabled
    void personalGoalCardPatternSerialization() throws Exception {
        // Gson Builder
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .setPrettyPrinting()
                .create();

        for (int i = 1; i <= 12; i++) {
            PersonalGoalCard pgc = new PersonalGoalCard(i);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
                    .concat("/model/pgc/")
                    .concat(String.valueOf(i))
                    .concat(".json")))) {

                bw.write(gson.toJson(pgc.getPattern()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // resource locator
            URI uri = ClassLoader.getSystemResource("model/pgc/"
                    .concat(String.valueOf(i))
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

            System.out.println(res);
        }
    }

    @Test
    void commonGoalCardSerialization() throws Exception {
        CommonGoalCard cgc = new CommonGoalCard(5, 2);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String res = gson.toJson(cgc);
        System.out.println(res);
        cgc = gson.fromJson(res, CommonGoalCard.class);
        System.out.println(cgc.getDescription());
    }

    @Test
    void moveSerialization1() throws Exception {
        Board b = new Board(2, new ArrayList<>());
        Shelf s = new Shelf();
        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        x.add(1);
        x.add(1);
        y.add(4);
        y.add(3);

        Move m = new Move(2, x, y, b, s);

        // interface adapters no more compulsory
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .registerTypeAdapter(BoardInterface.class, interfaceSerializer(Board.class))
                .registerTypeAdapter(ShelfInterface.class, interfaceSerializer(Shelf.class))
                .setPrettyPrinting()
                .create();

        String res = gson.toJson(m);
        System.out.println(res);

        m = null;
        m = gson.fromJson(res, Move.class);
    }

    @Test
    void moveSerialization2() throws Exception {
        Board b = new Board(2, new ArrayList<>());
        Shelf s = new Shelf();
        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        x.add(1);
        x.add(1);
        y.add(4);
        y.add(3);

        Move m = new Move(2, x, y, b, s);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .registerTypeAdapter(Move.class, new GsonMoveDeserializer(b, s))
                .setPrettyPrinting().create();

        String res = gson.toJson(m);
        System.out.println(res);

        m = null;
        m = gson.fromJson(res, Move.class);
    }

    @Test
    void clientMoveSerialization() throws Exception {
        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        x.add(3);
        x.add(3);
        x.add(3);
        y.add(3);
        y.add(4);
        y.add(2);
        ClientMove cm = new ClientMove(1, x, y);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting().create();

        String res = gson.toJson(cm);
        System.out.println(res);
    }

    @Test
    void statusSerialization() throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        String res = gson.toJson(new StatusMessage(Status.EOS, Status.EOS.getDescription()));
        res = gson.toJson(new StatusMessage(Status.EOS));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Sending_Identifier, "Game's name"));
        System.out.println(res.replace("\\u0027", "'"));

        res = gson.toJson(new StatusMessage(Status.Request_NumberOfPlayers, Status.Request_NumberOfPlayers.getDescription()));
        res = gson.toJson(new StatusMessage(Status.Request_NumberOfPlayers));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Response_NumberOfPlayers, "4"));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Request_Nickname, Status.Request_Nickname.getDescription()));
        res = gson.toJson(new StatusMessage(Status.Request_Nickname));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Response_Nickname, "Player3"));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Denied_Request, Status.Denied_Request.getDescription()));
        res = gson.toJson(new StatusMessage(Status.Denied_Request, "Nickname is already in use"));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Accepted_Request, Status.Accepted_Request.getDescription()));
        res = gson.toJson(new StatusMessage(Status.Accepted_Request));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.NotYourTurn, Status.NotYourTurn.getDescription()));
        res = gson.toJson(new StatusMessage(Status.NotYourTurn));
        System.out.println(res.replace("\\u0027", "'"));

        res = gson.toJson(new StatusMessage(Status.YourTurn, Status.YourTurn.getDescription()));
        res = gson.toJson(new StatusMessage(Status.YourTurn));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.SuccessfulMove, Status.SuccessfulMove.getDescription()));
        res = gson.toJson(new StatusMessage(Status.SuccessfulMove));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.FailedMove, Status.FailedMove.getDescription()));
        res = gson.toJson(new StatusMessage(Status.FailedMove));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.LastTurn, Status.LastTurn.getDescription()));
        res = gson.toJson(new StatusMessage(Status.LastTurn));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.MoveAllowed, Status.MoveAllowed.getDescription()));
        res = gson.toJson(new StatusMessage(Status.MoveAllowed));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.MoveNotAllowed, Status.MoveNotAllowed.getDescription()));
        res = gson.toJson(new StatusMessage(Status.MoveNotAllowed));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.GameEnded, Status.GameEnded.getDescription()));
        res = gson.toJson(new StatusMessage(Status.GameEnded));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.FinalScoreboard, Status.FinalScoreboard.getDescription()));
        res = gson.toJson(new StatusMessage(Status.FinalScoreboard));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.ForcedGameEnd, Status.ForcedGameEnd.getDescription()));
        res = gson.toJson(new StatusMessage(Status.ForcedGameEnd));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.InvalidStatus, Status.InvalidStatus.getDescription()));
        res = gson.toJson(new StatusMessage(Status.InvalidStatus));
        System.out.println(res);

        res = gson.toJson(new StatusMessage(Status.Error, "Reason of the error"));
        System.out.println(res);
    }

    @Test
    void clientModelSerialization() throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new GsonOptionalSerializer<>())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        x.add(3);
        x.add(3);
        x.add(3);
        y.add(3);
        y.add(4);
        y.add(2);
        ClientMove cm = new ClientMove(1, x, y);

        String res = gson.toJson(new StatusMessage(Status.Move_Request, gson.toJson(cm)));
        System.out.format(res.replace("\\n", System.lineSeparator()).replace("\\", ""));


        Board b = new Board(4, null);
        res = gson.toJson(new StatusMessage(Status.SendingBoard, gson.toJson(b)));
        System.out.println(res.replace("\\n", System.lineSeparator()).replace("\\", ""));

        Shelf s = new Shelf();
        List<ObjectCard> ocList = new ArrayList<>();
        for (int i = 0; i < Shelf.SHELF_DIM_X; i++) {
            for (int j = 0; j < Shelf.SHELF_DIM_Y; j++) {
                s.placeCard(new ObjectCard(ocList), j);
            }
        }
        res = gson.toJson(new StatusMessage(Status.SendingShelf, gson.toJson(s)));
        System.out.println(res.replace("\\n", System.lineSeparator()).replace("\\", ""));

        PersonalGoalCard pgc = new PersonalGoalCard(1);
        res = gson.toJson(new StatusMessage(Status.SendingPersonalGoalCard, gson.toJson(pgc)));
        System.out.println(res.replace("\\n", System.lineSeparator()).replace("\\", ""));

        CommonGoalCard cgc = new CommonGoalCard(5, 2);
        res = gson.toJson(new StatusMessage(Status.SendingCommonGoalCardSpecification, gson.toJson(cgc)));
        System.out.println(res.replace("\\n", System.lineSeparator()).replace("\\", ""));

        Map<String, Integer> sbMap = new HashMap<>();
        sbMap.put("Player 1", 10);
        sbMap.put("Player 4", 40);
        sbMap.put("Player 3", 30);
        sbMap.put("Player 2", 20);
        Scoreboard sb = new Scoreboard(sbMap);
        res = gson.toJson(new StatusMessage(Status.FinalScoreboard, gson.toJson(sb)));
        System.out.println(res.replace("\\n", System.lineSeparator()).replace("\\", ""));
    }
}
