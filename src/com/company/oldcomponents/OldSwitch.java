package com.company.oldcomponents;

import com.company.geometry.Point;
import com.company.main.OldMain;
import processing.core.PConstants;

import java.util.ArrayList;

public class OldSwitch extends OldComponent {

    public OldSwitch(OldMain mainSketch, int id, String name, String type, char orientation, int normalState, int xPos, int yPos, int length, String label, String textAnchor, char labelOrientation, char labelPlacement, String associatedWith) {
        super(mainSketch, id, name, type, orientation, normalState, xPos, yPos, length, label, textAnchor, labelOrientation, labelPlacement, associatedWith);
        calcDrawingCoords();
    } // END Constructor #0

    public OldSwitch(OldMain mainSketch, int id, String name, String type, char orientation, int normalState, OldComponent connectedTo, String inout, int length, String label, String textAnchor, char labelOrientation, char labelPlacement, String associatedWith) {
        super(mainSketch, id, name, type, orientation, normalState, connectedTo, inout, length, label, textAnchor, labelOrientation, labelPlacement, associatedWith);
        calcDrawingCoords();
    } // END Constructor #1

    public OldSwitch(OldMain mainSketch, int id, String name, String type, char orientation, int normalState, OldComponent connectedToIn, String inoutIn, OldComponent connectedToOut, String inoutOut, String label, String textAnchor, char labelOrientation, char labelPlacement, String associatedWith) {
        super(mainSketch, id, name, type, orientation, normalState, connectedToIn, inoutIn, connectedToOut, inoutOut, label, textAnchor, labelOrientation, labelPlacement, associatedWith);
        calcDrawingCoords();
    } // END Constructor #2

    public void renderEnergy(float scale, float panX, float panY) {

        float strokeWt = mainSketch.STROKE_FAT * scale;

        // Set drawing parameters
        mainSketch.stroke(252, 252, 3);  // yellow
        mainSketch.strokeWeight(strokeWt);
        mainSketch.strokeCap(PConstants.SQUARE);

        // Draw top energy highlight T if present
        if (getInNode().isEnergized()) {
            drawLine(0, 2);
            drawLine(4, 6);
        }

        // Draw bottom energy highlight T if present
        if (getOutNode().isEnergized()) {
            drawLine(1, 3);
            drawLine(5, 7);
        }

        // Draw energy backslash if present
        if (getNormalState() == 0 && getCurrentState() == 0 && (getInNode().isEnergized() || getOutNode().isEnergized())) {
            if (getOrientation() == 'D' || getOrientation() == 'U') drawLine(9, 10);
            else drawLine(8, 11);
        }

        // Draw colored X (the other slash) if present
        if (getNormalState() == 1 && getCurrentState() == 0 && (getInNode().isEnergized() || getOutNode().isEnergized())) {
            if (getOrientation() == 'D' || getOrientation() == 'U') drawLine(8, 11);
            else drawLine(9, 10);
        }

    } // END renderEnergy()

    public void renderLines(float scale, float panX, float panY) {

        float strokeWt = mainSketch.STROKE_THIN * scale;
        float unit = mainSketch.UNIT * scale;

        int x = calcPos((int) (getInNode().getCoord().getX()), scale, panX);
        int y = calcPos((int) (getInNode().getCoord().getY()), scale, panY);

        // Set drawing parameters
        mainSketch.stroke(0);  // black
        mainSketch.strokeWeight(strokeWt);
        mainSketch.strokeCap(PConstants.SQUARE);

        // Draw top black T regardless
        drawLine(0, 2);
        drawLine(4, 6);

        // Draw bottom black T regardless
        drawLine(1, 3);
        drawLine(5, 7);

        // Draw black backslash if present
        if (getNormalState() == 0 && getCurrentState() == 0) {
            if (getOrientation() == 'D' || getOrientation() == 'U') drawLine(9, 10);
            else drawLine(8, 11);
        }

        // Draw red X if present
        if (getNormalState() == 1 && getCurrentState() == 0) {
            mainSketch.stroke(255, 0, 0); // red
            drawLine(8, 11);
            drawLine(9, 10);
        }

        // Place yellow dot if locked out
        float xCir = getDs().get(12).getX();
        float yCir = getDs().get(12).getY();
        xCir = calcPos(xCir, scale, panX);
        yCir = calcPos(yCir, scale, panY);

        if(getCurrentState() == 2) {
            mainSketch.fill(252,252,3); // yellow
            mainSketch.stroke(0); // black
            mainSketch.ellipse( xCir, yCir, 1.2f * unit, 1.2f * unit);
            mainSketch.fill(255,0,0); // black
            drawText(12, "LOCK", "CC", 'H');
        }
        // Draw green dot if present
        if(getNormalState() == 0 && getCurrentState() == 1) {
            mainSketch.stroke(0, 255, 0); // green line
            mainSketch.fill(0, 255, 0); // green fill

            mainSketch.ellipse(xCir, yCir, unit/2, unit/2);
        }

        // Place text/label at dS(13)
        mainSketch.fill(0); // black
        if(getLabelPlacement() == 'R') {
            drawText(13, getLabel(), getTextAnchor(), getLabelOrientation());
        } else {
            drawText(14, getLabel(), getTextAnchor(), getLabelOrientation());
        }

    } // END renderLines()

    // Establishes all of the grid coordinates needed to draw this object at the correct orientation
    public void calcDrawingCoords() {

        ArrayList<Point> points = new ArrayList<>();
        float x = this.getInNode().getCoord().getX();
        float y = this.getInNode().getCoord().getY();

        // This step puts the inNode in the ArrayList as element #0.  This is important!
        points.add(this.getInNode().getCoord());

        // This next step puts the outNode in the ArrayList as element #1.  This is also important!
        points.add(this.getOutNode().getCoord());

        // These next steps define specific points
        Point point;
        point = new Point(x, y + 1.25f); // Element #2 - bottom of top vertical line
        points.add(point);
        point = new Point(x, y + 1.75f); // Element #3 - top of bottom vertical line
        points.add(point);
        point = new Point(x - 0.5f, y + 1.25f); // Element #4 - left of top horizontal line
        points.add(point);
        point = new Point(x - 0.5f, y + 1.75f); // Element #5 - left of bottom horiz line
        points.add(point);
        point = new Point(x + 0.5f, y + 1.25f); // Element #6 - right of top horizontal line
        points.add(point);
        point = new Point(x + 0.5f, y + 1.75f); // Element #7 - right of bottom horiz line
        points.add(point);
        point = new Point(x - 0.5f, y + 1f); // Element #8 - top-left of cross
        points.add(point);
        point = new Point(x - 0.5f, y + 2f); // Element #9 - bottom-left of cross
        points.add(point);
        point = new Point(x + 0.5f, y + 1f); // Element #10 - top-right of cross
        points.add(point);
        point = new Point(x + 0.5f, y + 2f); // Element #11 - bottom-right of cross
        points.add(point);
        point = new Point(x, y + 1.5f); // Element #12 - centerpoint of dot (for normally open)
        points.add(point);
        point = new Point(x + 0.75f, y + 1.5f); // Element #13 - Right text label anchorpoint
        points.add(point);
        point = new Point(x - 0.75f, y + 1.5f); // Element #14 - Left text label anchorpoint
        points.add(point);

        // This next step determines if the coordinates have to be rotated, rotates them, and
        // then executes the setDs() method for this component.
        switch (this.getOrientation()) {
            case 'U':
                setDs(rotateDwgCoords(points, 180));
                break;
            case 'L':
                setDs(rotateDwgCoords(points, 90));
                break;
            case 'R':
                setDs(rotateDwgCoords(points, 270));
                break;
            default:
                setDs(points);
                break;
        } // END switch (orientation)

//        // Update the location of the outNode
//        this.getOutNode().setCoord(getDs().get(1));

        // Establish the click coordinates
        this.setClickCoords(8, 11);

    } // END calcDrawingCoords

} // END public class Switch
