import controller.Controller;
import controller.Simulator;
import view.MainView;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Controller controller = new Controller();
        MainView view = new MainView(controller);
        view.view();
    }
}
