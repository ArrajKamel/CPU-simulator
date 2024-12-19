package controller;

import java.io.FileNotFoundException;

public class Controller {
    public Simulator simulator;
    public Controller() throws FileNotFoundException
    {
        simulator = new Simulator();
    }



}
