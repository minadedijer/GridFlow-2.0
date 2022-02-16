package construction.properties.objectData;

import construction.properties.Visitor;

public class WireData extends CloseableData {

    //private boolean underground;

    public WireData(String name, boolean namePos, double angle, boolean defaultState) {

        super(name, namePos, defaultState, angle);
        //this.underground = underground;
    }

    @Override
    public void accept(Visitor v) {
        v.setWireMenu(this);
    }
//
//    @Override
//    public ObjectData applySettings(String name, boolean nameRight, boolean isClosed, String label, String subLabel, String acronym) {
//        return new WireData(name, nameRight, getAngle(), isClosed); //using isClosed to toggle underground
//    }
}
