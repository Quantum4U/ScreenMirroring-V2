package engine.app.server.v2;

import java.util.ArrayList;

/**
 * Created by Rakesh Rajput on 18/04/18.
 */

public class GameServiceV2ResponseHandler {
    private static final GameServiceV2ResponseHandler ourInstance = new GameServiceV2ResponseHandler();
    private ArrayList<GameProvidersResponce> gameProvidersResponces = new ArrayList<>();

    private GameServiceV2ResponseHandler() {
    }

    public static GameServiceV2ResponseHandler getInstance() {
        return ourInstance;
    }

    public ArrayList<GameProvidersResponce> getGameV2FeaturesListResponse() {
        return this.gameProvidersResponces;
    }

    void setGameV2FeaturesListResponse(ArrayList<GameProvidersResponce> gameProvidersResponces) {
        this.gameProvidersResponces = gameProvidersResponces;
    }

}
