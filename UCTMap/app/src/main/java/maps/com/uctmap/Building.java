package maps.com.uctmap;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by Noosrat on 2014/08/07.
 */
public class Building extends CustomOverlay {

    public String faculty;
    public int photo;
    int colour;
    public int clicked=0;

    PolygonOptions rectOptions;
    Polygon polygon;

    /**
     * Building Constructor
     * @param n Name of Building
     * @param d Description of Building
     * @param f Building Faculty
     * @param p Photo of building
     */
    public Building(String n, String d, String f, int p)
    {
        super(n,d);
        photo = p;
        faculty=f;

        if (f.equals("Science"))
            colour = Color.rgb(128, 0, 128);
        else if (f.equals("Commerce"))
            colour = Color.YELLOW;
        else if (f.equals("Humanities"))
            colour = Color.BLUE;
        else if (f.equals("Engineering and Built Environment"))
            colour = Color.rgb(0, 204, 0);
    }
    /**
     * Set Parking Polygon
     * @param mMap Google Maps Object
     * @param lngs Coordinates of boundary of parking
     */
    public void setPolygon(GoogleMap mMap, LatLng...lngs)
    {
        rectOptions = new PolygonOptions()
                .add(lngs).fillColor(colour).strokeWidth(0);
        polygon = mMap.addPolygon(rectOptions);
    }
    /**
     * Hide Polygon for building
     */
    public void hidePolygon()
    {
        polygon.setVisible(false);
    }

    /**
     * Show Polygon for building
     */
    public void showPolygon()
    {
        polygon.setVisible(true);
    }

}