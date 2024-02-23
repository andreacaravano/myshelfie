package it.polimi.ingsw.Server.Model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ObjectCardInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents all the ObjectCards' definition methods
 */
public class ObjectCard implements ObjectCardInterface {
    /**
     * Maximum amount of cards that can be extracted, used for validation in Board reset methods
     * LIMIT = Number of Object Cards Types (detected dynamically), multiplied for 22 (based on rulebook, total number of cards for type)
     */
    public static final int LIMIT = (ObjectCardType.values().length) * 22;
    private static final int TYPESLIMIT = 6;
    private static final int IMAGESLIMIT = 3;
    /**
     * Card's type
     */
    @Expose
    private ObjectCardType type;
    /**
     * Card's image indicated using an Integer attribute (1-3)
     */
    @Expose
    private int image;

    /**
     * ObjectCard's constructor
     *
     * @param list (1st parameter) List that contains all the objectcards used in the game
     */
    ObjectCard(List<ObjectCard> list) throws Exception {
        ArrayList<Integer> counters = new ArrayList<>();
        // counts types
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            counters.add((int) list
                    .stream()
                    .filter(x -> x.getType().equals(ObjectCardType.values()[finalI]))
                    .count());
        }
        // extracts type and image
        Random finder = new Random();
        boolean endCycle = false;
        while (!endCycle) {
            int position = finder.ints(0, TYPESLIMIT).findFirst().getAsInt();
            if (counters.get(position) < 22) {
                this.type = ObjectCardType.values()[position];
                this.image = finder.nextInt(1, IMAGESLIMIT + 1);
                endCycle = true;
                list.add(this);
            } else {
                if (list.size() >= LIMIT)
                    throw new Exception();
                else continue;
            }
        }
    }

    /**
     * ObjectCard's constructor, used only for testing
     *
     * @param type type of the new instance of ObjectCard
     */
    ObjectCard(ObjectCardType type) throws RemoteException {
        this.type = type;
    }

    /**
     * This method return objectcard's type
     *
     * @return type card's type
     */
    public ObjectCardType getType() {
        return type;
    }

    /**
     * This method returns Object Card's image
     *
     * @return int image
     */
    public int getImage() {
        return image;
    }
}
