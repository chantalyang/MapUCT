package maps.com.uctmap;


import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Noosrat on 2014/08/14.
 */
public class Parking extends CustomOverlay {

    int colour;
    String type;

    PolygonOptions rectOptions;
    Polygon polygon;

    ArrayList<Polyline> lines = new ArrayList<Polyline>();

    /**
     * Parking Constructor
     * @param n Name of parking
     * @param d Description of parking
     * @param t Type of parking (Staff, Standard, Postgrad)
     */

    public Parking(String n, String d, String t)
    {
        super(n, d);
        type = t;

        if (t.equals("Staff"))
            colour = Color.RED;
        else if (t.equals("PostGrad"))
            colour = Color.YELLOW;
        else if (t.equals("Standard"))
            colour = Color.BLUE;
    }

    /**
     * Set Parking Polygon
     * @param mMap Google Maps Object
     * @param lngs Coordinates of boundary of parking
     */
    public void setPolygon(GoogleMap mMap, LatLng...lngs)
    {
        rectOptions = new PolygonOptions()
                .add(lngs).fillColor(Color.WHITE).strokeWidth(4).strokeColor(colour);
        polygon = mMap.addPolygon(rectOptions);


    }
    /**
     * Draw Fill for Parking Polygon
     * @param mMap Google Maps Object
     * @param points Points to draw lines
     */
    public void setPolylines(GoogleMap mMap, LatLng[] points)
    {
        for (int i=0; i<points.length-1; i+=2)
        {
            Polyline line = mMap.addPolyline(new PolylineOptions()
            .add(points[i], points[i+1])
            .width(4)
            .color(colour));

        lines.add(line);
        }
    }

    /**
     * Hide Polygon for parking
     */
    public void hidePolygon()
    {
        for (int k=0; k<lines.size(); k++)
            lines.get(k).setVisible(false);

        polygon.setVisible(false);
    }

    /**
     * Show Polygon for parking
     */
    public void showPolygon()
    {
        for (int k=0; k<lines.size(); k++)
            lines.get(k).setVisible(true);

        polygon.setVisible(true);
    }
}
