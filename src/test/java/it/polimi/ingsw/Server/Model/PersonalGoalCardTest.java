package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Client.View.PersonalGoalCardView;
import it.polimi.ingsw.Common.ObjectCardInterface;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;

import static it.polimi.ingsw.Server.Model.Shelf.SHELF_DIM_X;
import static it.polimi.ingsw.Server.Model.Shelf.SHELF_DIM_Y;

/**
 * Tests of Personal Goal Cards
 */
public class PersonalGoalCardTest {
    /**
     * Used to check functionality of initialization and evaluation of Personal Goal Cards in the Shelf
     */
    @Test
    void pgCardEvaluation() throws Exception {
        for (int t = 1; t <= 12; t++) {
            PersonalGoalCard pgCard = new PersonalGoalCard(t);
            assert Arrays.deepEquals(pgCard.getCopy().getPattern(), pgCard.getPattern());
            System.out.println("TYPE C" + t);

            PersonalGoalCardView.print(pgCard, new PrintWriter(System.out));

            Optional<ObjectCardType>[][] patternCheck = new Optional[SHELF_DIM_X][SHELF_DIM_Y];

            for (Optional<ObjectCardType>[] r : patternCheck)
                Arrays.fill(r, Optional.empty());

            // resource locator
            URI uri = new File("src/test/res/".concat("personalgoalcards.txt")).toURI();

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

            Scanner s = new Scanner(new FileReader(Paths.get(uri).toFile()));
            while (s.hasNext()) {
                String readFromFile = s.next();

                if ((readFromFile.length() == 2 || readFromFile.length() == 3) &&
                        Integer.parseInt(readFromFile.substring(1)) == t) {
                    for (int i = 0; i < 6; i++) {
                        ObjectCardType c = ObjectCardType.values()[ObjectCardInterface.getOrdinal(s.next().charAt(0))];
                        patternCheck[s.nextInt()][s.nextInt()] = Optional.of(c);
                    }
                }
            }

            // close FileSystem
            if (fs != null) fs.close();

            for (int i = 0; i < SHELF_DIM_X; i++) {
                for (int j = 0; j < SHELF_DIM_Y; j++) {
                    if (patternCheck[i][j].isPresent()) {
                        assert pgCard.getPlainType(i, j) != null;
                        if (pgCard.getPattern()[i][j].isEmpty())
                            assert false;
                        else assert patternCheck[i][j].get() == pgCard.getPattern()[i][j].get();
                    }
                }
            }
        }
    }
}