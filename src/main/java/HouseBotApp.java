import frontend.HouseBot;
import util.Constants;

public class HouseBotApp {

    public static void main(String[] args) throws Exception {
        Constants.init();

        var houseBot = new HouseBot();
        houseBot.start();
    }
}
