package model.components;

import model.geometry.Point;

import java.util.List;

public class Turbine extends Source {

    public Turbine(String name, Point position) {
        super(name, position);
    }

    public void connectWire(Wire wire) {
        if (super.getOutputCount() < 2) {
            super.addOutput(wire);
        }
    }
}