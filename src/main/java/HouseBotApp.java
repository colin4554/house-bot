import frontend.HouseBot;
import util.Constants;

public class HouseBotApp {

    public static void main(String[] args) throws Exception {
        Constants.init();

        var cleanupCoordinator = new HouseBot();
        cleanupCoordinator.start();
    }

}
