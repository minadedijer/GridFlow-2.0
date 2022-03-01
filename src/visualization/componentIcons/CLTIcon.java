package visualization.componentIcons;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CLTIcon extends DeviceIcon {

    // TODO: add clickable bounding box

    // shapes that are energized when the in node is energized
    private final Group inNodeEnergyOutline = new Group();

    // shapes that are energized when the out node is energized
    private final Group outNodeEnergyOutline = new Group();

    // shapes that are energized when both nodes are energized
    private final Group midNodeEnergyOutline = new Group();

    // static shapes do not have an energy outline

    //CLT variables
    private final Text buildingText = new Text();
    private final Text transformerText = new Text();
    private final Text warningText = new Text();

    private final AnchorPane cltBuildingNamePositioner = new AnchorPane();
    private final AnchorPane cltTransformerNamePositioner = new AnchorPane();
    private final AnchorPane cltWarningNamePositioner = new AnchorPane();

    private int offset1 = 30;
    private int offset2 = 40;
    private int offset3 = 50;

    public CLTIcon() {
        addEnergyOutlineNode(inNodeEnergyOutline, outNodeEnergyOutline, midNodeEnergyOutline);
    }

    public void setBuildingText(String name, boolean right) {
        if (name == null || name.isEmpty()) return;
        if (cltBuildingNamePositioner.getParent() == null) {
            // Add nodes to hierarchy
            cltBuildingNamePositioner.getChildren().add(buildingText);
            getComponentNode().getChildren().add(cltBuildingNamePositioner);

            // Setup AnchorPane
            cltBuildingNamePositioner.setLayoutX(getBoundingRect().getX());
            cltBuildingNamePositioner.setLayoutY(getBoundingRect().getY()+offset2);
            cltBuildingNamePositioner.setPrefWidth(getBoundingRect().getWidth());
            cltBuildingNamePositioner.setPrefHeight(getBoundingRect().getHeight());
            cltBuildingNamePositioner.setMaxWidth(getBoundingRect().getWidth());
            cltBuildingNamePositioner.setMaxHeight(getBoundingRect().getHeight());
            cltBuildingNamePositioner.setMinWidth(getBoundingRect().getWidth());
            cltBuildingNamePositioner.setMinHeight(getBoundingRect().getHeight());

            // Setup Text
            //componentName.setWrappingWidth(Globals.UNIT * 5);
            buildingText.setFont(Font.font(null, 10));
        }

        setComponentNamePosition(right);
        buildingText.setText(name);
    }

    public void setTransformerText(String name, boolean right) {
        if (name == null || name.isEmpty()) return;
        if (cltTransformerNamePositioner.getParent() == null) {
            // Add nodes to hierarchy
            cltTransformerNamePositioner.getChildren().add(transformerText);
            getComponentNode().getChildren().add(cltTransformerNamePositioner);

            // Setup AnchorPane
            cltTransformerNamePositioner.setLayoutX(getBoundingRect().getX());
            cltTransformerNamePositioner.setLayoutY(getBoundingRect().getY()+offset1);
            cltTransformerNamePositioner.setPrefWidth(getBoundingRect().getWidth());
            cltTransformerNamePositioner.setPrefHeight(getBoundingRect().getHeight());
            cltTransformerNamePositioner.setMaxWidth(getBoundingRect().getWidth());
            cltTransformerNamePositioner.setMaxHeight(getBoundingRect().getHeight());
            cltTransformerNamePositioner.setMinWidth(getBoundingRect().getWidth());
            cltTransformerNamePositioner.setMinHeight(getBoundingRect().getHeight());

            // Setup Text
            //componentName.setWrappingWidth(Globals.UNIT * 5);
            transformerText.setFont(Font.font(null, 10));
        }

        setComponentNamePosition(right);
        transformerText.setText(name);
    }

    public void setWarningText(String name, boolean right) {
        if (name == null || name.isEmpty()) return;
        if (cltWarningNamePositioner.getParent() == null) {
            // Add nodes to hierarchy
            cltWarningNamePositioner.getChildren().add(warningText);
            getComponentNode().getChildren().add(cltWarningNamePositioner);

            // Setup AnchorPane
            cltWarningNamePositioner.setLayoutX(getBoundingRect().getX());
            cltWarningNamePositioner.setLayoutY(getBoundingRect().getY()+offset3);
            cltWarningNamePositioner.setPrefWidth(getBoundingRect().getWidth());
            cltWarningNamePositioner.setPrefHeight(getBoundingRect().getHeight());
            cltWarningNamePositioner.setMaxWidth(getBoundingRect().getWidth());
            cltWarningNamePositioner.setMaxHeight(getBoundingRect().getHeight());
            cltWarningNamePositioner.setMinWidth(getBoundingRect().getWidth());
            cltWarningNamePositioner.setMinHeight(getBoundingRect().getHeight());

            // Setup Text
            //componentName.setWrappingWidth(Globals.UNIT * 5);
            warningText.setFont(Font.font(null, 10));
            warningText.setFill(Color.BLUE);
        }

        setComponentNamePosition(right);
        warningText.setText(name);
        warningText.setFill(Color.BLUE);
    }

    public Text getBuildingText() {
        return buildingText;
    }

    public Text getTransformerText() {
        return transformerText;
    }

    public Text getWarningText() {
        return warningText;
    }
}
