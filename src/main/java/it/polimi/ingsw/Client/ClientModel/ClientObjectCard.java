package it.polimi.ingsw.Client.ClientModel;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.Common.ObjectCardInterface;

/**
 * Object Card (Client)
 */
public class ClientObjectCard implements ObjectCardInterface {
    @Expose
    private ClientObjectCardType type;
    @Expose
    private int image;

    /**
     * This method return objectcard's type
     *
     * @return type card's type
     */
    public ClientObjectCardType getType() {
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
