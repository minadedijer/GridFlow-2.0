package construction.properties;

import construction.ComponentType;
import construction.properties.objectData.*;
import construction.selector.observable.Observer;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class PropertiesMenuViewController implements Observer<String>, Visitor {

    private List<String> selectedIDs;

    private PropertiesMenuFunctions propertiesMenuFunctions;

    // JavaFX Elements
    public AnchorPane PropertiesWindow;

    public HBox nameControl;
    public HBox namePosControl;
    public HBox defaultStateControl;
    public HBox labelControl;
    public HBox subLabelControl;
    public HBox acronymControl;
    public HBox linkBreakersControl;

    public TextField nameField;
    public Label numSelectedLabel;

    public ToggleButton toggleLeft;
    public ToggleButton toggleRight;
    public ToggleButton toggleOpen;
    public ToggleButton toggleClosed;

    public Button applyButton;

    ToggleGroup namePosTG = new ToggleGroup();
    ToggleGroup defStateTG = new ToggleGroup();

    public void initialize() {
        PropertiesWindow.managedProperty().bind(PropertiesWindow.visibleProperty());
        nameControl.managedProperty().bind(nameControl.visibleProperty());
        namePosControl.managedProperty().bind(namePosControl.visibleProperty());
        defaultStateControl.managedProperty().bind(defaultStateControl.visibleProperty());
        labelControl.managedProperty().bind(labelControl.visibleProperty());
        subLabelControl.managedProperty().bind(subLabelControl.visibleProperty());
        acronymControl.managedProperty().bind(acronymControl.visibleProperty());
        linkBreakersControl.managedProperty().bind(linkBreakersControl.visibleProperty());

        PropertiesWindow.setVisible(false);
        hideAllControls();

        toggleLeft.setToggleGroup(namePosTG);
        toggleRight.setToggleGroup(namePosTG);
        namePosTG.selectToggle(toggleRight);

        toggleOpen.setToggleGroup(defStateTG);
        toggleClosed.setToggleGroup(defStateTG);
    }

    private void hideAllControls() {
        nameControl.setVisible(false);
        namePosControl.setVisible(false);
        defaultStateControl.setVisible(false);
        labelControl.setVisible(false);
        subLabelControl.setVisible(false);
        acronymControl.setVisible(false);
        linkBreakersControl.setVisible(false);
        applyButton.setVisible(false);
    }

    @Override
    public void onListUpdate(List<String> newList) {
        selectedIDs = newList;
        setupMenu();
    }

    private void setupMenu() {
        hideAllControls();

        if (selectedIDs.size() > 1 && allSelectedItemsAreBreakers()) {
            setLinkBreakersMenu();
            return;
        }
        if (selectedIDs.size() != 1) return;
        if (!PropertiesWindow.isVisible()) PropertiesWindow.setVisible(true);

        // Get the data of the selected object
        ObjectData objectData = propertiesMenuFunctions.getObjectData(selectedIDs.get(0));
        if (objectData == null) {
            System.err.println("Selected ID did not match a component");
            return;
        }
        applyButton.setVisible(true);

        // Determine which menu to display
        // This runs the correct setMenu() function depending on the object
        // This is the Visitor pattern / Double Dispatch
        objectData.accept(this);
    }

    private boolean allSelectedItemsAreBreakers() {
        for (String ID : selectedIDs) {
            ComponentType type = propertiesMenuFunctions.getComponentType(ID);
            if (type != ComponentType.BREAKER_12KV && type != ComponentType.BREAKER_70KV) {
                return false;
            }
        }
        return true;
    }

    public void setComponentMenu(ComponentData data) {
        // Show correct controls
        nameControl.setVisible(true);
        namePosControl.setVisible(true);

        // Fill in data
        nameField.setText(data.getName());
    }

    public void setCloseableMenu(CloseableData data) {
        // Show correct controls
        nameControl.setVisible(true);
        namePosControl.setVisible(true);
        defaultStateControl.setVisible(true);

        // Fill in data
        nameField.setText(data.getName());
        defStateTG.selectToggle(data.isClosed() ? toggleClosed : toggleOpen);

    }

    public void setBreakerMenu(BreakerData data) {
        // Show correct controls
        nameControl.setVisible(true);
        namePosControl.setVisible(true);
        defaultStateControl.setVisible(true);
        // Unlink button if it is linked

        // Fill in data
        nameField.setText(data.getName());
        defStateTG.selectToggle(data.isClosed() ? toggleClosed : toggleOpen);
    }

    public void setAssociationMenu(AssociationData data) {
        // Show correct controls
        labelControl.setVisible(true);
        subLabelControl.setVisible(true);
        acronymControl.setVisible(true);
    }

    private void setLinkBreakersMenu() {
        // Show correct controls
        linkBreakersControl.setVisible(true);

        // Fill in data
        numSelectedLabel.setText("" + selectedIDs.size());
    }

    public void setPropertiesWindowVisibility(boolean visible) {
        PropertiesWindow.setVisible(visible);
    }

    public void setPropertiesMenuFunctions(PropertiesMenuFunctions pmf) {
        this.propertiesMenuFunctions = pmf;
    }


}
