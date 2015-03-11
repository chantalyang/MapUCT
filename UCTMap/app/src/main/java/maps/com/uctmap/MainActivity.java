package maps.com.uctmap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    String d = "default Description";
    String[] f = {"Science", "Commerce", "Humanities", "Engineering and Built Environment"};
    String[] t = {"Staff", "PostGrad", "Standard"};

    List<Building> buildings = new ArrayList<Building>();
    List<Parking> parking = new ArrayList<Parking>();
    List<Icon> pointsOfInterest = new ArrayList<Icon>(){}; //Array of Icons

    ArrayList<LatLng> markerPoints;
    Fragment[] fragments = new Fragment[3];
    MapView mapView;
    GoogleMap mMap;

    private static final int INFORMATION=0;
    private static final int FILTER=1;
    private static final int ROUTES=2;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if(position==0){// selection of tabs content
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            MapInformationFragment.newInstance(position + 1)).commit();
        }else if(position==1){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            MapFilterFragment.newInstance(position + 1)).commit();
        }else if(position==2){
            // Display the fragment as the main content.
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            MapRoutesFragment.newInstance(position +1)).commit();
        }else{

        }
    }

    //Set action bar title
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "View Information";
                break;
            case 2:
                mTitle = "Filter";
                break;
            case 3:
                mTitle = "Find Routes";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }



}
