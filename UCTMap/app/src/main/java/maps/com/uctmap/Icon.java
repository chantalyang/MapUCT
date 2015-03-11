package maps.com.uctmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Chantal on 2014/08/13.
 */
public class Icon extends CustomOverlay{

    private String iconType;
    private int icon_bmp; //Icon image reference
    private Marker mark;

    /**
     * Constructor
     * @param name Name of building
     * @param type Type of icon
     * @param  desc Description of the icon to be shown in info window
     */

    public Icon(String name, String type, String desc){
        super(name, desc);
        iconType = type;

        //Set Appropriate Icon Bitmaps
        if (iconType.equals("postOffice")) {
            icon_bmp = R.drawable.ic_postoffice;
        }
        else if (iconType.equals("atm"))
            icon_bmp = R.drawable.ic_atm;
        else if (iconType.equals("food"))
            icon_bmp = R.drawable.ic_food;
        else if (iconType.equals("jammie"))
            icon_bmp = R.drawable.ic_jammie;
        else if (iconType.equals("library"))
            icon_bmp = R.drawable.ic_library;
        else if (iconType.equals("info"))
            icon_bmp = R.drawable.ic_info;
        else if (iconType.equals("rideLink"))
            icon_bmp = R.drawable.ic_ridelink;
        else if (iconType.equals("juta"))
            icon_bmp = R.drawable.ic_juta;
        else if (iconType.equals("cps"))
            icon_bmp = R.drawable.ic_cps;

            //Parking
        else if (iconType.equals("parkingBlue"))
            icon_bmp = R.drawable.ic_parking_blue;
        else if (iconType.equals("parkingRed"))
            icon_bmp = R.drawable.ic_parking_red;
        else if (iconType.equals("parkingYel"))
            icon_bmp = R.drawable.ic_parking_yellow;



    }

    /**
     * Add Icon to the Map
     *
     * @param map Google Maps Object
     * @param coOrds LatLng coordinates of marker
     */
    public void placeIcon(GoogleMap map, LatLng coOrds){

       mark = map.addMarker(new MarkerOptions()
                .position(coOrds)
                .icon(BitmapDescriptorFactory.fromResource(icon_bmp))
                .title(name)
                .snippet(description));
    }

    /**
     * Hide marker
     * @param  //marker
     */
    public void hideIcon(){

        mark.setVisible(false);

    }

    /**
     * Show marker
     */
    public void showIcon(){

        mark.setVisible(true);

    }

    /**
     * Get Icon Type from Icon Object
     * @return String iconType
     */
    public String getIconType(){
        return iconType;
    }

}