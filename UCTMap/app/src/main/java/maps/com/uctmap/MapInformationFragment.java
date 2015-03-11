package maps.com.uctmap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapInformationFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {


    String d = "default Description";
    String[] f = {"Science", "Commerce", "Humanities", "Engineering and Built Environment"};
    String[] t = {"Staff", "PostGrad", "Standard"};
    int[] photos = {R.drawable.uct, R.drawable.maths, R.drawable.centilivres, R.drawable.em, R.drawable.ls, R.drawable.cs};
    LatLng[] points;
    Marker oldMarker = null;

    String [] iconDetails;
    String [] coords;
    Double lat = 0.0;
    Double lng = 0.0;


    boolean started = false;
    MapView mapView;
    List<LatLng> uctBounds;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    List<Building> buildings = new ArrayList<Building>();
    List<Parking> parking = new ArrayList<Parking>();
    List<Icon> iconList = new ArrayList<Icon>();

    ArrayList<LatLng> markerPoints; //For routes

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MapInformationFragment newInstance(int sectionNumber) {
        MapInformationFragment fragment = new MapInformationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;

    }


    public MapInformationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        setRetainInstance(true);

        mapView = (MapView) v.findViewById(R.id.mapview);


        mapView.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
        MapsInitializer.initialize(getActivity());
        mMap = mapView.getMap();
        mMap.getUiSettings();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);


        uctBounds = new ArrayList<LatLng>();

        uctBounds.add(new LatLng(-33.95981321958577, 18.462960943579674));
        uctBounds.add(new LatLng(-33.96079681093532, 18.46111323684454));
        uctBounds.add(new LatLng(-33.961690570538316, 18.460381999611855));
        uctBounds.add(new LatLng(-33.961330175761006, 18.458857163786888));
        uctBounds.add(new LatLng(-33.96038774732505, 18.45753885805607));
        uctBounds.add(new LatLng(-33.9586572001746, 18.45749594271183));
        uctBounds.add(new LatLng(-33.95686905164669, 18.458314687013626));
        uctBounds.add(new LatLng(-33.954349728541814, 18.459762409329414));
        uctBounds.add(new LatLng(-33.95405298914054, 18.461464941501617));
        uctBounds.add(new LatLng(-33.95381604184653, 18.46431076526642));
        uctBounds.add(new LatLng(-33.955425991163956, 18.464558199048042));
        uctBounds.add(new LatLng(-33.957172177448484, 18.464220240712166));

        /////----------------------------------Zooming camera to position user-----------------

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {

            if (isCurrentLocationUCT(new LatLng(location.getLatitude(), location.getLongitude()))) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(18)                   // Sets the zoom
                        .bearing(270)                // Sets the orientation of the camera to east          // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {

                CameraPosition cameraPos = new CameraPosition.Builder().target(new LatLng(-33.957798800000000000, 18.461580899999944000)).bearing(270).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
            }

        } else {

            CameraPosition cameraPos = new CameraPosition.Builder().target(new LatLng(-33.957798800000000000, 18.461580899999944000)).bearing(270).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }

/////----------------------------------Zooming camera to position user-----------------


        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("buildings.txt")));

            //System.out.println(reader.readLine());

            String[] line;

            //RW JAMES
            line = reader.readLine().split("~");
            Building rw = new Building(line[0], line[1], line[2], photos[0]);
            rw.setPolygon(mMap, new LatLng(-33.95527108805417, 18.461837768554688), new LatLng(-33.95596217066635, 18.461838774383068), new LatLng(-33.95596217066635, 18.461594693362713), new LatLng(-33.9559349168105, 18.461594693362713), new LatLng(-33.9559349168105, 18.461554795503616), new LatLng(-33.95527080995138, 18.46155244857073), new LatLng(-33.95527080995138, 18.461642302572727), new LatLng(-33.955305016587076, 18.46164297312498), new LatLng(-33.95530640710043, 18.46174020320177), new LatLng(-33.95527108805417, 18.461742214858532));
            buildings.add(rw);

            //Molecular
            line = reader.readLine().split("~");
            Building molecular = new Building(line[0], line[1], line[2], photos[0]);
            molecular.setPolygon(mMap, new LatLng(-33.954969067894524, 18.461293280124664), new LatLng(-33.95548522692853, 18.46130132675171), new LatLng(-33.955483002111826, 18.461206443607807), new LatLng(-33.95553695390048, 18.461209125816822), new LatLng(-33.955539178715775, 18.461079709231853), new LatLng(-33.95563401141355, 18.46107702702284), new LatLng(-33.95564235446107, 18.46080109477043), new LatLng(-33.955138989129125, 18.460786677896976), new LatLng(-33.95513008982576, 18.46102774143219), new LatLng(-33.9550313631165, 18.46102975308895), new LatLng(-33.95502913828792, 18.461146764457226), new LatLng(-33.95497351755474, 18.46114207059145));
            buildings.add(molecular);

            //Maths
            line = reader.readLine().split("~");
            Building maths = new Building(line[0], line[1], line[2], photos[1]);
            maths.setPolygon(mMap, new LatLng(-33.95697667533483, 18.461483046412468), new LatLng(-33.957012271770196, 18.461769707500935), new LatLng(-33.957515626019806, 18.461687564849854), new LatLng(-33.95748225455952, 18.46139620989561));
            buildings.add(maths);

            //Computer Science
            line = reader.readLine().split("~");
            Building cs = new Building(line[0], line[1], line[2], photos[5]);
            cs.setPolygon(mMap, new LatLng(-33.95654812645575, 18.46092212945223), new LatLng(-33.95658344497192, 18.46126914024353), new LatLng(-33.95670386153494, 18.46125941723585), new LatLng(-33.95670386153494, 18.46123293042183), new LatLng(-33.95701032509053, 18.461196720600128), new LatLng(-33.957013106061474, 18.461221866309643), new LatLng(-33.95713880585348, 18.46120811998844), new LatLng(-33.957104599955024, 18.46085373312235));
            buildings.add(cs);

            //PD Hahn
            line = reader.readLine().split("~");
            Building pd = new Building(line[0], line[1], line[2], photos[0]);
            pd.setPolygon(mMap, new LatLng(-33.956432715454234, 18.460001461207867), new LatLng(-33.956445508006695, 18.46073303371668), new LatLng(-33.956053387589805, 18.460744433104992), new LatLng(-33.95604949418653, 18.460556007921696), new LatLng(-33.95601167254548, 18.460561372339725), new LatLng(-33.95601167254548, 18.460536561906338), new LatLng(-33.955962726867405, 18.460536561906338), new LatLng(-33.95596662027465, 18.460744433104992), new LatLng(-33.95560119541865, 18.460752479732037), new LatLng(-33.955592852367076, 18.460543602705002), new LatLng(-33.95541792619752, 18.46054896712303), new LatLng(-33.955415701379046, 18.460426591336727), new LatLng(-33.95557672246506, 18.460419215261936), new LatLng(-33.95558117209352, 18.46039440482855), new LatLng(-33.955698252858426, 18.46039440482855), new LatLng(-33.955698252858426, 18.460419215261936), new LatLng(-33.95573524033499, 18.46041653305292), new LatLng(-33.95573384982864, 18.460235483944416), new LatLng(-33.95596050206318, 18.460230119526386), new LatLng(-33.95596439547053, 18.46041653305292), new LatLng(-33.956065902099105, 18.460412174463272), new LatLng(-33.956065902099105, 18.460464142262936), new LatLng(-33.956084534809555, 18.460464142262936), new LatLng(-33.95607786040628, 18.460073545575142), new LatLng(-33.95619549668727, 18.460068181157112), new LatLng(-33.95619772148534, 18.460197933018208), new LatLng(-33.956311186109936, 18.460197933018208), new LatLng(-33.95630673651966, 18.460006825625896));
            buildings.add(pd);

            //NSLT
            line = reader.readLine().split("~");
            Building nslt = new Building(line[0], line[1], line[2], photos[0]);
            nslt.setPolygon(mMap, new LatLng(-33.955880130971806, 18.461298644542694), new LatLng(-33.95566960841063, 18.461298644542694), new LatLng(-33.955668496004705, 18.461025394499302), new LatLng(-33.955709655014566, 18.461025394499302), new LatLng(-33.955709655014566, 18.460954651236534), new LatLng(-33.95584314355822, 18.460954651236534), new LatLng(-33.95584314355822, 18.46102673560381), new LatLng(-33.95588040907259, 18.461026065051556));
            buildings.add(nslt);

            //Hoeri
            line = reader.readLine().split("~");
            Building hoeri = new Building(line[0], line[1], line[2], photos[0]);
            hoeri.setPolygon(mMap, new LatLng(-33.956504743061494, 18.46017077565193), new LatLng(-33.956517535603126, 18.460326343774796), new LatLng(-33.956825946513774, 18.46029281616211), new LatLng(-33.95681649119137, 18.460174798965454), new LatLng(-33.956874335500196, 18.460166081786156), new LatLng(-33.956865714475896, 18.460061475634575), new LatLng(-33.95685375627945, 18.460061475634575), new LatLng(-33.95684430096014, 18.459926024079323), new LatLng(-33.956730837046486, 18.459938764572144), new LatLng(-33.956735008516574, 18.459991738200188), new LatLng(-33.95661542629281, 18.46000414341688), new LatLng(-33.956607917635935, 18.45991227775812), new LatLng(-33.9565453454696, 18.459918648004532), new LatLng(-33.9565636999765, 18.46016474068165));
            buildings.add(hoeri);

            //GEO
            line = reader.readLine().split("~");
            Building geo = new Building(line[0], line[1], line[2], photos[0]);
            geo.setPolygon(mMap, new LatLng(-33.95880570102158, 18.460349813103676), new LatLng(-33.95884268714727, 18.46049264073372), new LatLng(-33.95890692616946, 18.46046917140484), new LatLng(-33.95892249925844, 18.460527509450912), new LatLng(-33.958853254608805, 18.460556343197823), new LatLng(-33.95889552444179, 18.460720293223858), new LatLng(-33.95910993228437, 18.46063781529665), new LatLng(-33.95906655019641, 18.460472859442234), new LatLng(-33.958977005047444, 18.4605073928833), new LatLng(-33.95896059769627, 18.460444025695324), new LatLng(-33.959045137234575, 18.46040915697813), new LatLng(-33.9590081511969, 18.460270017385483));
            buildings.add(geo);

            //EM
            line = reader.readLine().split("~");
            Building em = new Building(line[0], line[1], line[2], photos[3]);
            em.setPolygon(mMap, new LatLng(-33.95823060641206, 18.46060059964657), new LatLng(-33.95830902863318, 18.46093386411667), new LatLng(-33.95842388090456, 18.460898995399475), new LatLng(-33.95842026570226, 18.46087921410799), new LatLng(-33.95872922589867, 18.460769578814507), new LatLng(-33.958733675362254, 18.46079170703888), new LatLng(-33.958839071962764, 18.46075415611267), new LatLng(-33.958762596869846, 18.460419215261936));
            buildings.add(em);

            //Snape
            line = reader.readLine().split("~");
            Building snape = new Building(line[0], line[1], line[2], photos[0]);
            snape.setPolygon(mMap, new LatLng(-33.959014547279764, 18.459251448512077), new LatLng(-33.958980342135426, 18.45909621566534), new LatLng(-33.958958651061174, 18.459101915359497), new LatLng(-33.958930841983516, 18.45897551625967), new LatLng(-33.95874396474589, 18.459034860134125), new LatLng(-33.95875759122501, 18.459100909531116), new LatLng(-33.958682228425396, 18.459125719964504), new LatLng(-33.95867110475929, 18.459072411060333), new LatLng(-33.95833739409961, 18.459179028868675), new LatLng(-33.958355192034496, 18.459263183176517), new LatLng(-33.9583251580172, 18.45927458256483), new LatLng(-33.95836742811259, 18.459459990262985));
            buildings.add(snape);

            //Engineering
            line = reader.readLine().split("~");
            Building eng = new Building(line[0], line[1], line[2], photos[0]);
            eng.setPolygon(mMap, new LatLng(-33.958292343060265, 18.459480106830597), new LatLng(-33.95780540098302, 18.459671214222908), new LatLng(-33.95773949256081, 18.459474742412567), new LatLng(-33.95775645633681, 18.459462337195873), new LatLng(-33.95775645633681, 18.459422774612904), new LatLng(-33.958226435015284, 18.45923099666834));
            buildings.add(eng);

            // Menzies
            line = reader.readLine().split("~");
            Building men = new Building(line[0], line[1], line[2], photos[0]);
            men.setPolygon(mMap, new LatLng(-33.958911097532855, 18.45988143235445), new LatLng(-33.9587845660854, 18.459922671318054), new LatLng(-33.95878845936351, 18.459938764572144), new LatLng(-33.95860992456967, 18.459998108446598), new LatLng(-33.95860464082396, 18.459983356297016), new LatLng(-33.95839940876289, 18.460051752626896), new LatLng(-33.95843750743495, 18.460229448974133), new LatLng(-33.95875564458527, 18.460129871964455), new LatLng(-33.95894724934042, 18.460045382380486));
            buildings.add(men);

            //EGS
            line = reader.readLine().split("~");
            Building egs = new Building(line[0], line[1], line[2], photos[0]);
            egs.setPolygon(mMap, new LatLng(-33.95733013615387, 18.459700047969818), new LatLng(-33.957351827643336, 18.459880091249943), new LatLng(-33.95732485232868, 18.459885455667973), new LatLng(-33.9573621171941, 18.460189551115036), new LatLng(-33.957086801758514, 18.460236825048923), new LatLng(-33.95705454251785, 18.45997329801321), new LatLng(-33.95702478613847, 18.45997966825962), new LatLng(-33.956997532622985, 18.4597597271204), new LatLng(-33.95705648919652, 18.459747321903706), new LatLng(-33.95705537680871, 18.45972016453743), new LatLng(-33.95723085580462, 18.45968931913376), new LatLng(-33.95723697392473, 18.459715135395527));
            buildings.add(egs);

            //Arts
            line = reader.readLine().split("~");
            Building arts = new Building(line[0], line[1], line[2], photos[0]);
            arts.setPolygon(mMap, new LatLng(-33.958049845697985, 18.46127785742283), new LatLng(-33.958107689168266, 18.461555130779743), new LatLng(-33.95859462951539, 18.461407274007797), new LatLng(-33.95853567400771, 18.46113134175539));
            buildings.add(arts);

            //Beattie
            line = reader.readLine().split("~");
            Building beattie = new Building(line[0], line[1], line[2], photos[0]);
            beattie.setPolygon(mMap, new LatLng(-33.95871254040817, 18.46106629818678), new LatLng(-33.95878484417669, 18.461343236267567), new LatLng(-33.959332682272276, 18.461139053106308), new LatLng(-33.959261213238584, 18.46086211502552));
            buildings.add(beattie);

            //HGS
            line = reader.readLine().split("~");
            Building hgs = new Building(line[0], line[1], line[2], photos[0]);
            hgs.setPolygon(mMap, new LatLng(-33.96025843720902, 18.460064493119717), new LatLng(-33.96017389887639, 18.46011444926262), new LatLng(-33.96016194114492, 18.46009500324726), new LatLng(-33.960025678504884, 18.46017614006996), new LatLng(-33.96002122910887, 18.460199609398842), new LatLng(-33.95994113994094, 18.460248559713364), new LatLng(-33.95989970489044, 18.46015401184559), new LatLng(-33.95990776943204, 18.460146635770798), new LatLng(-33.959860216434265, 18.460029624402523), new LatLng(-33.959868559067374, 18.460019901394844), new LatLng(-33.959864665838694, 18.460004813969135), new LatLng(-33.959852151888136, 18.460015542805195), new LatLng(-33.95981739090474, 18.45992535352707), new LatLng(-33.959866890540816, 18.459890484809875), new LatLng(-33.9598196156081, 18.459774143993855), new LatLng(-33.96000482195909, 18.459661826491356), new LatLng(-33.96005682427041, 18.45978856086731), new LatLng(-33.96013079541786, 18.459743969142437));
            buildings.add(hgs);

            //Centlivres
            line = reader.readLine().split("~");
            Building cent = new Building(line[0], line[1], line[2], photos[2]);
            cent.setPolygon(mMap, new LatLng(-33.95943168207968, 18.46078298985958), new LatLng(-33.95952651043888, 18.461060263216496), new LatLng(-33.95960799044021, 18.46101935952902), new LatLng(-33.95958546525993, 18.460953645408154), new LatLng(-33.95965304078286, 18.46091877669096), new LatLng(-33.95967500976846, 18.460979461669922), new LatLng(-33.959973119999674, 18.460830599069595), new LatLng(-33.95994948256611, 18.460762202739716), new LatLng(-33.96002373189415, 18.460724651813507), new LatLng(-33.96004848165576, 18.46079707145691), new LatLng(-33.96013107350476, 18.460754491388798), new LatLng(-33.960039304778704, 18.460482247173786), new LatLng(-33.95996088415254, 18.460520803928375), new LatLng(-33.95998146262172, 18.460582830011845), new LatLng(-33.95991750249849, 18.460615016520023), new LatLng(-33.95989692401384, 18.46055433154106), new LatLng(-33.959592695565355, 18.460703529417515), new LatLng(-33.95960326293373, 18.460737392306328), new LatLng(-33.95958768996936, 18.460747450590134), new LatLng(-33.95959686689514, 18.460777290165424), new LatLng(-33.95954653283552, 18.4608044475317), new LatLng(-33.959523729550085, 18.46073605120182));
            buildings.add(cent);

            //LC
            line = reader.readLine().split("~");
            Building lc = new Building(line[0], line[1], line[2], photos[0]);
            lc.setPolygon(mMap, new LatLng(-33.959594642185955, 18.459717482328415), new LatLng(-33.959058763665446, 18.459989055991173), new LatLng(-33.958980064044766, 18.459760397672653), new LatLng(-33.95910603902098, 18.459698036313057), new LatLng(-33.95910993228437, 18.4597148001194), new LatLng(-33.959409434941996, 18.459565602242947), new LatLng(-33.95940498551375, 18.459548838436604), new LatLng(-33.95951677732771, 18.459496200084686));
            buildings.add(lc);

            //LS
            line = reader.readLine().split("~");
            Building ls = new Building(line[0], line[1], line[2], photos[4]);
            ls.setPolygon(mMap, new LatLng(-33.95979152872387, 18.46028745174408), new LatLng(-33.95924897728904, 18.460558019578457), new LatLng(-33.959086016520665, 18.460093326866627), new LatLng(-33.95926315986675, 18.46000414341688), new LatLng(-33.95934769910441, 18.460252583026886), new LatLng(-33.9595209486612, 18.46016574651003), new LatLng(-33.95943640959571, 18.45991227775812), new LatLng(-33.95962856899475, 18.45981203019619));
            buildings.add(ls);

            //Botany
            line = reader.readLine().split("~");
            Building bot = new Building(line[0], line[1], line[2], photos[0]);
            bot.setPolygon(mMap, new LatLng(-33.95613097751783, 18.461559154093266), new LatLng(-33.95614154531617, 18.461835086345673), new LatLng(-33.95623415254551, 18.461825363337994), new LatLng(-33.95623415254551, 18.461755625903606), new LatLng(-33.95631480140185, 18.461750261485577), new LatLng(-33.956318694792984, 18.461802899837494), new LatLng(-33.95679535576104, 18.46177238970995), new LatLng(-33.956781172771954, 18.461514227092266), new LatLng(-33.95630451172444, 18.461546413600445), new LatLng(-33.95630590222146, 18.461628556251526), new LatLng(-33.956226087655665, 18.461635932326317), new LatLng(-33.95622386285834, 18.46155647188425));
            buildings.add(bot);

            //ZOO
            line = reader.readLine().split("~");
            Building zoo = new Building(line[0], line[1], line[2], photos[0]);
            zoo.setPolygon(mMap, new LatLng(-33.955932692005554, 18.460945934057236), new LatLng(-33.9559349168105, 18.461136370897293), new LatLng(-33.95597524139004, 18.461135029792786), new LatLng(-33.95597607569149, 18.46118364483118), new LatLng(-33.95596578597311, 18.461182303726673), new LatLng(-33.95596689837515, 18.461285568773746), new LatLng(-33.95619994628334, 18.46127651631832), new LatLng(-33.95619688718606, 18.461164869368076), new LatLng(-33.956293943946335, 18.461159840226173), new LatLng(-33.956297003040135, 18.46127148717642), new LatLng(-33.95650808024646, 18.461263440549374), new LatLng(-33.956503908765235, 18.461151458323002), new LatLng(-33.95652587856402, 18.461151458323002), new LatLng(-33.95651225172747, 18.460949957370758), new LatLng(-33.95644634230353, 18.460952639579773), new LatLng(-33.95644522990774, 18.46093889325857), new LatLng(-33.956039482577296, 18.460953310132027), new LatLng(-33.956039482577296, 18.46094224601984));
            buildings.add(zoo);

        } catch (IOException e) {
            e.printStackTrace();
        }


        //SportsCentre1
        Parking sc1 = new Parking("Sports Centre PG", d, t[1]);
        sc1.setPolygon(mMap, new LatLng(-33.955934638709884, 18.46337165683508), new LatLng(-33.95616129040956, 18.4636452421546), new LatLng(-33.95554640936507, 18.463555723428726), new LatLng(-33.95515511911413, 18.46374347805977), new LatLng(-33.95502607914854, 18.46345581114292), new LatLng(-33.95573162501845, 18.463391438126564), new LatLng(-33.95574052425891, 18.463436029851437));
        LatLng[] points0 = {new LatLng(-33.95610900761707, 18.46357684582472), new LatLng(-33.956080085207425, 18.463633842766285), new LatLng(-33.956063955397774, 18.4635242074728), new LatLng(-33.95601417544871, 18.46362579613924), new LatLng(-33.95600972584292, 18.46346251666546), new LatLng(-33.95593352630741, 18.463609032332897), new LatLng(-33.955972738485656, 18.46341222524643), new LatLng(-33.95590460383812, 18.463606350123882), new LatLng(-33.95588819589443, 18.463384732604027), new LatLng(-33.95579336347997, 18.463590927422047), new LatLng(-33.955777233615976, 18.46342496573925), new LatLng(-33.955700755770884, 18.46357148140669), new LatLng(-33.95567795145468, 18.463394455611706), new LatLng(-33.95559340857055, 18.463558740913868), new LatLng(-33.95553778820622, 18.463404178619385), new LatLng(-33.95548216780555, 18.463583886623383), new LatLng(-33.95541125174195, 18.463425636291504), new LatLng(-33.955368423972956, 18.463639207184315), new LatLng(-33.955298064019956, 18.463431000709534), new LatLng(-33.955266916512294, 18.463684804737568), new LatLng(-33.955217970405805, 18.463433012366295), new LatLng(-33.95514538550287, 18.463696539402008), new LatLng(-33.955104504323366, 18.463448099792004), new LatLng(-33.95507363484828, 18.46354566514492)};
        sc1.setPolylines(mMap, points0);
        parking.add(sc1);


        //SportsCentre2
        Parking sc2 = new Parking("Sports Centre S", d, t[2]);
        sc2.setPolygon(mMap, new LatLng(-33.95500855862089, 18.463048450648785), new LatLng(-33.95501745793698, 18.463447093963623), new LatLng(-33.955156787733095, 18.4637401252985), new LatLng(-33.95455969815839, 18.464108258485794), new LatLng(-33.954353622022865, 18.46385981887579), new LatLng(-33.95427936774645, 18.463581204414368), new LatLng(-33.95428242691268, 18.46330761909485), new LatLng(-33.954679561376075, 18.463148698210716), new LatLng(-33.95499215050441, 18.463069908320904));
        LatLng[] points3 = {new LatLng(-33.955125083967204, 18.463665693998337), new LatLng(-33.95510005467, 18.463771976530552), new LatLng(-33.955081421743955, 18.46358623355627), new LatLng(-33.95503191932362, 18.463816568255424), new LatLng(-33.955030250702215, 18.46346352249384), new LatLng(-33.95493541733154, 18.463876582682133), new LatLng(-33.95501328638268, 18.46334919333458), new LatLng(-33.95483390935432, 18.463930897414684), new LatLng(-33.95500967103545, 18.46313763409853), new LatLng(-33.95477022346568, 18.463973812758923), new LatLng(-33.95500967103545, 18.463050797581673), new LatLng(-33.954596964234874, 18.464085794985294), new LatLng(-33.954877571704564, 18.463100753724575), new LatLng(-33.954564147840024, 18.464105241000652), new LatLng(-33.95480137115516, 18.46312053501606), new LatLng(-33.954448734147434, 18.463965766131878), new LatLng(-33.954677614643025, 18.463150039315224), new LatLng(-33.95438643849891, 18.463888987898827), new LatLng(-33.954638958077624, 18.463166803121567), new LatLng(-33.95433387650999, 18.463771641254425), new LatLng(-33.95457304720259, 18.463191613554955), new LatLng(-33.95430717834446, 18.46365462988615), new LatLng(-33.9544559648894, 18.463241569697857), new LatLng(-33.95428492986678, 18.46358221024275), new LatLng(-33.95438337933642, 18.463269397616386), new LatLng(-33.954286320396804, 18.463420271873474), new LatLng(-33.95431746826341, 18.46329353749752), new LatLng(-33.954286320396804, 18.463343493640423)};
        sc2.setPolylines(mMap, points3);
        parking.add(sc2);

        //North
        Parking north = new Parking("North", d, t[2]);
        north.setPolygon(mMap, new LatLng(-33.955472712333815, 18.46228938549757), new LatLng(-33.95539011596255, 18.462525084614754), new LatLng(-33.95530696330576, 18.462559282779694), new LatLng(-33.95511284742281, 18.462511003017426), new LatLng(-33.95491150039438, 18.462535813450813), new LatLng(-33.954828347269874, 18.462514355778694), new LatLng(-33.95475882118366, 18.462449982762337), new LatLng(-33.95464757932755, 18.46227664500475), new LatLng(-33.95461365053249, 18.46192829310894), new LatLng(-33.954655088157416, 18.461783453822136), new LatLng(-33.954763270854876, 18.461590334773064), new LatLng(-33.954775229345124, 18.461345583200455), new LatLng(-33.954828347269874, 18.461300991475582), new LatLng(-33.95488619293031, 18.4612724930048), new LatLng(-33.955018014144194, 18.461853191256523), new LatLng(-33.95506696036572, 18.461978249251842), new LatLng(-33.955096717429804, 18.4620426222682), new LatLng(-33.95516318410547, 18.462106995284557), new LatLng(-33.9552329879642, 18.46215158700943), new LatLng(-33.95526107635334, 18.462212271988392), new LatLng(-33.955471599925296, 18.462291061878204));
        LatLng[] points4 = {new LatLng(-33.95539873713636, 18.462260887026787), new LatLng(-33.955308353819085, 18.462559282779694), new LatLng(-33.95529639540375, 18.462230376899242), new LatLng(-33.95516763375555, 18.46251469105482), new LatLng(-33.95516763375555, 18.462110683321953), new LatLng(-33.955019404662245, 18.462523743510246), new LatLng(-33.955090599155795, 18.462001718580723), new LatLng(-33.954908163146804, 18.462523743510246), new LatLng(-33.95465063848054, 18.462277315557003), new LatLng(-33.954625330938875, 18.46203424036503), new LatLng(-33.95493513922766, 18.461476676166058), new LatLng(-33.95477050157011, 18.461501486599445), new LatLng(-33.95490260106721, 18.461328148841858), new LatLng(-33.95477801038912, 18.46137274056673), new LatLng(-33.954767164317005, 18.46158966422081), new LatLng(-33.954956831327706, 18.461581952869892), new LatLng(-33.95498241687449, 18.461693599820137), new LatLng(-33.954681508109076, 18.461737520992756), new LatLng(-33.9546167096867, 18.46192292869091), new LatLng(-33.95499632205974, 18.461760990321636), new LatLng(-33.95501829224781, 18.46185050904751), new LatLng(-33.95463589892422, 18.462111353874207), new LatLng(-33.95502913828792, 18.46187498420477), new LatLng(-33.95467233065311, 18.462311513721943), new LatLng(-33.95473379177871, 18.462403044104576), new LatLng(-33.95504721501838, 18.46192393451929)};
        north.setPolylines(mMap, points4);
        parking.add(north);

        //South
        Parking south = new Parking("South", d, t[2]);
        south.setPolygon(mMap, new LatLng(-33.960525399813214, 18.460509404540062), new LatLng(-33.959845477780426, 18.46146896481514), new LatLng(-33.959820171783925, 18.4615296497941), new LatLng(-33.959803764595364, 18.461608439683914), new LatLng(-33.95981572237718, 18.461663760244846), new LatLng(-33.95982907059675, 18.46169762313366), new LatLng(-33.959852708063764, 18.461733497679234), new LatLng(-33.95988246345408, 18.461758643388748), new LatLng(-33.959931406877196, 18.461772724986076), new LatLng(-33.9599653335517, 18.461772724986076), new LatLng(-33.96002623467937, 18.461772724986076), new LatLng(-33.960084076805906, 18.46174255013466), new LatLng(-33.96014609019598, 18.46169762313366), new LatLng(-33.96019197451378, 18.46164397895336), new LatLng(-33.960236468374084, 18.46157994121313), new LatLng(-33.960278181346986, 18.461524285376072), new LatLng(-33.96029875973941, 18.46146896481514), new LatLng(-33.96034464397488, 18.461399227380753), new LatLng(-33.960389137755335, 18.461281210184097), new LatLng(-33.960418892958025, 18.46113134175539), new LatLng(-33.96044697963514, 18.461022041738033), new LatLng(-33.960480906104024, 18.460958003997803), new LatLng(-33.960541806862594, 18.460882902145386), new LatLng(-33.96059519927261, 18.46080243587494), new LatLng(-33.960690026334866, 18.46069883555174), new LatLng(-33.9607656654407, 18.460618369281292), new LatLng(-33.9608262879108, 18.460548631846905), new LatLng(-33.960867722509995, 18.460450395941734), new LatLng(-33.960884129493365, 18.460344783961773), new LatLng(-33.960851593608005, 18.460280410945415), new LatLng(-33.96080988091627, 18.460246548056602), new LatLng(-33.960719503347335, 18.460250236093998), new LatLng(-33.96060409800435, 18.46041988581419));
        LatLng[] points5 = {new LatLng(-33.960720893772205, 18.46024889498949), new LatLng(-33.960846866170634, 18.460500352084637), new LatLng(-33.96061160630853, 18.460410498082638), new LatLng(-33.960695309951056, 18.460690788924694), new LatLng(-33.96047590056029, 18.460579812526703), new LatLng(-33.960563775618695, 18.46085038036108), new LatLng(-33.96035910445608, 18.460743092000484), new LatLng(-33.96048062801827, 18.460958674550056), new LatLng(-33.96022673534413, 18.460930846631527), new LatLng(-33.96041221889478, 18.461172245442867), new LatLng(-33.96018418808583, 18.460989519953728), new LatLng(-33.96038941584139, 18.461279533803463), new LatLng(-33.960079071238816, 18.461141400039196), new LatLng(-33.96030626807055, 18.461458571255207), new LatLng(-33.95996394311448, 18.46130032092333), new LatLng(-33.96015026149861, 18.461690917611122), new LatLng(-33.95989942680278, 18.461394533514977), new LatLng(-33.96002623467937, 18.46177238970995)};
        south.setPolylines(mMap, points5);
        parking.add(south);

        //Ring1
        Parking ring1 = new Parking("Ring Staff", d, t[0]);
        ring1.setPolygon(mMap, new LatLng(-33.96081822345625, 18.45984388142824), new LatLng(-33.960950035479414, 18.459500558674335), new LatLng(-33.960995919363604, 18.459540121257305), new LatLng(-33.96102873327772, 18.459559567272663), new LatLng(-33.96108184729834, 18.459570296108723), new LatLng(-33.96114413804465, 18.459570296108723), new LatLng(-33.961169443647314, 18.459559567272663), new LatLng(-33.96109074597918, 18.459977991878986), new LatLng(-33.96104180322304, 18.45996357500553), new LatLng(-33.960984239831795, 18.459927700459957), new LatLng(-33.960843529155866, 18.459852933883667));
        LatLng[] points6 = {new LatLng(-33.9611374640383, 18.459569290280342), new LatLng(-33.96115581755452, 18.459630645811558), new LatLng(-33.961052926579185, 18.45956426113844), new LatLng(-33.961124116024024, 18.45980565994978), new LatLng(-33.960992860438765, 18.459535762667656), new LatLng(-33.96109797615667, 18.45993608236313), new LatLng(-33.960926120233, 18.45956292003393), new LatLng(-33.961052926579185, 18.45996256917715), new LatLng(-33.96099814403614, 18.459931388497353), new LatLng(-33.96089803371408, 18.459637686610222), new LatLng(-33.960863273158026, 18.45972988754511), new LatLng(-33.96090248306423, 18.45988042652607), new LatLng(-33.96083435236457, 18.459808342158794), new LatLng(-33.96084770042431, 18.45985159277916)};
        ring1.setPolylines(mMap, points6);
        parking.add(ring1);

        //Ring2
        Parking ring2 = new Parking("Ring S", d, t[2]);
        ring2.setPolygon(mMap, new LatLng(-33.96094947931097, 18.459498211741447), new LatLng(-33.96091249410121, 18.459463343024254), new LatLng(-33.96088802267539, 18.45943920314312), new LatLng(-33.96086521974954, 18.45941137522459), new LatLng(-33.96085103743892, 18.459386564791203), new LatLng(-33.96084658808607, 18.45937918871641), new LatLng(-33.96062578865907, 18.459623269736767), new LatLng(-33.9606533190975, 18.4596698731184), new LatLng(-33.96069169484527, 18.459725193679333), new LatLng(-33.96072506504667, 18.459760062396526), new LatLng(-33.960768168204076, 18.45979928970337), new LatLng(-33.96081989196416, 18.459839522838593));
        LatLng[] points7 = {new LatLng(-33.96086633208753, 18.459411039948463), new LatLng(-33.96082295089521, 18.45940701663494), new LatLng(-33.96090554199232, 18.459455631673336), new LatLng(-33.96077567649379, 18.45945931971073), new LatLng(-33.96094169295235, 18.459489159286022), new LatLng(-33.960733407594965, 18.45950525254011), new LatLng(-33.960920002378174, 18.4595749899745), new LatLng(-33.96070977037265, 18.459531739354134), new LatLng(-33.96087773355107, 18.45968797802925), new LatLng(-33.96062717908547, 18.45962092280388), new LatLng(-33.960856042960586, 18.459745310246944), new LatLng(-33.960690026334866, 18.45971915870905), new LatLng(-33.96083574278756, 18.459797613322735), new LatLng(-33.960767612034445, 18.459795266389847)};
        ring2.setPolylines(mMap, points7);
        parking.add(ring2);

        //Nursery1
        Parking nurse1 = new Parking("Nursery Middle", d, t[1]);
        nurse1.setPolygon(mMap, new LatLng(-33.95847421562839, 18.45845751464367), new LatLng(-33.95851120189819, 18.458630852401257), new LatLng(-33.958951420701844, 18.458387777209282), new LatLng(-33.95902261190528, 18.458382412791252), new LatLng(-33.95970698992724, 18.458273448050022), new LatLng(-33.959661105347855, 18.458084017038345), new LatLng(-33.95949675492406, 18.45811787992716), new LatLng(-33.959203370552565, 18.458143025636673), new LatLng(-33.959046249596355, 18.458168171346188), new LatLng(-33.95848172412055, 18.458346873521805), new LatLng(-33.95844028835925, 18.458371683955193));
        LatLng[] points8 = {new LatLng(-33.9595810158409, 18.458100110292435), new LatLng(-33.95961216176923, 18.458287864923477), new LatLng(-33.95946755557697, 18.45811989158392), new LatLng(-33.95948841225449, 18.45830798149109), new LatLng(-33.95939497429937, 18.45812425017357), new LatLng(-33.95940721022791, 18.45832172781229), new LatLng(-33.959276786262706, 18.4581346437335), new LatLng(-33.95928902220824, 18.45833983272314), new LatLng(-33.95918863178493, 18.45814436674118), new LatLng(-33.95918446043514, 18.45835693180561), new LatLng(-33.958987294401645, 18.45818594098091), new LatLng(-33.958986182039084, 18.458386100828648), new LatLng(-33.9588543669731, 18.45822650939226), new LatLng(-33.9588699400717, 18.458433374762535), new LatLng(-33.95870836903504, 18.458277136087418), new LatLng(-33.95873729055124, 18.458505794405937), new LatLng(-33.95864107085352, 18.458295576274395), new LatLng(-33.958666933384116, 18.45854803919792), new LatLng(-33.95852065703225, 18.45833547413349), new LatLng(-33.95856459558262, 18.458601348102093)};
        nurse1.setPolylines(mMap, points8);
        parking.add(nurse1);

        //Nursery2
        Parking nurse2 = new Parking("Nursery Top", d, t[1]);
        nurse2.setPolygon(mMap, new LatLng(-33.95834796162389, 18.45815509557724), new LatLng(-33.95773754589779, 18.45844980329275), new LatLng(-33.95772558782389, 18.45844443887472), new LatLng(-33.957688601212546, 18.45843907445669), new LatLng(-33.95766802218876, 18.458469584584236), new LatLng(-33.9576721936129, 18.45850009471178), new LatLng(-33.957693050730555, 18.45851417630911), new LatLng(-33.95773003733996, 18.45850344747305), new LatLng(-33.95774199541323, 18.45847863703966), new LatLng(-33.95835241110743, 18.458215780556202));
        LatLng[] points9 = {new LatLng(-33.9583034667759, 18.458176888525486), new LatLng(-33.95830569151886, 18.458236567676067), new LatLng(-33.95821892650061, 18.458214439451694), new LatLng(-33.958221151245766, 18.458273448050022), new LatLng(-33.95815301839875, 18.458246625959873), new LatLng(-33.95815301839875, 18.458308316767216), new LatLng(-33.958081270280466, 18.458281494677067), new LatLng(-33.95807904553165, 18.45833580940962), new LatLng(-33.95800034500467, 18.458319045603275), new LatLng(-33.95800034500467, 18.458370678126812), new LatLng(-33.95793221198084, 18.45835290849209), new LatLng(-33.957934436733495, 18.458395823836327), new LatLng(-33.957876593145414, 18.458383418619633), new LatLng(-33.957876593145414, 18.45842532813549), new LatLng(-33.957810684778366, 18.458410911262035), new LatLng(-33.95780846002248, 18.458450138568878), new LatLng(-33.95775284110615, 18.4584404155612), new LatLng(-33.95775284110615, 18.458472602069378), new LatLng(-33.95769138216133, 18.458437733352184), new LatLng(-33.957703340240045, 18.4585128352046)};
        nurse2.setPolylines(mMap, points9);
        parking.add(nurse2);

        //Nursery3
        Parking nurse3 = new Parking("Nursery Bottom", d, t[1]);
        nurse3.setPolygon(mMap, new LatLng(-33.95836742811259, 18.4588460996747), new LatLng(-33.95673139324251, 18.459543138742447), new LatLng(-33.95674919151337, 18.45959309488535), new LatLng(-33.95670775490827, 18.459610864520073), new LatLng(-33.95664267993069, 18.459610864520073), new LatLng(-33.956609864324825, 18.45959309488535), new LatLng(-33.95659234412329, 18.459532409906387), new LatLng(-33.95657732680481, 18.459482118487358), new LatLng(-33.956600965175284, 18.459414392709732), new LatLng(-33.956657419139376, 18.45935370773077), new LatLng(-33.95670775490827, 18.45935370773077), new LatLng(-33.95679952722797, 18.45932487398386), new LatLng(-33.95836742811259, 18.458645939826965));
        LatLng[] points = {new LatLng(-33.957471408832106, 18.45903553068638), new LatLng(-33.95741662398276, 18.459239043295383), new LatLng(-33.95824367678726, 18.458699248731136), new LatLng(-33.95823644636718, 18.45889739692211), new LatLng(-33.95813271758072, 18.45874920487404), new LatLng(-33.95810018064309, 18.4589584171772), new LatLng(-33.958033438167995, 18.45878306776285), new LatLng(-33.95800980019551, 18.458997644484043), new LatLng(-33.957914970145744, 18.45885280519724), new LatLng(-33.95788521406728, 18.45905665308237), new LatLng(-33.95779038387865, 18.45890276134014), new LatLng(-33.95776229632449, 18.459097556769848), new LatLng(-33.957663016479515, 18.45895305275917), new LatLng(-33.957630201267186, 18.459145836532116), new LatLng(-33.957538429843474, 18.459003008902073), new LatLng(-33.957526749836994, 18.45919579267502), new LatLng(-33.95741551159967, 18.4590620175004), new LatLng(-33.957378524853546, 18.459265530109406), new LatLng(-33.957471408832106, 18.45903553068638), new LatLng(-33.95741662398276, 18.459239043295383), new LatLng(-33.95735127145137, 18.45908548682928), new LatLng(-33.9572889779279, 18.459300063550472), new LatLng(-33.95722696245523, 18.45914449542761), new LatLng(-33.95717356793132, 18.459344655275345), new LatLng(-33.957083186499396, 18.4592105448246), new LatLng(-33.95702534233258, 18.459419757127762), new LatLng(-33.95696749812642, 18.45925882458687), new LatLng(-33.9569185529982, 18.45946803689003), new LatLng(-33.95685208769377, 18.45930341631174), new LatLng(-33.95680314249914, 18.459517993032932), new LatLng(-33.956724440791945, 18.4593590721488), new LatLng(-33.956678554603975, 18.459602147340775), new LatLng(-33.956637117964476, 18.459383882582188), new LatLng(-33.956595681304805, 18.45953408628702)};
        nurse3.setPolylines(mMap, points);

        //Eng Mall
        Parking engMall = new Parking("Engineering Mall", d, t[0]);
        engMall.setPolygon(mMap, new LatLng(-33.95980320841942, 18.45909621566534), new LatLng(-33.95979125063586, 18.459185734391212), new LatLng(-33.959126061516564, 18.45943048596382), new LatLng(-33.95907266818451, 18.459475077688694), new LatLng(-33.95901037592144, 18.45964305102825), new LatLng(-33.95866804575085, 18.459803983569145), new LatLng(-33.958508142884, 18.45958974212408), new LatLng(-33.959035682158806, 18.459300063550472), new LatLng(-33.959047361958135, 18.459182046353817), new LatLng(-33.95902817371554, 18.45909621566534), new LatLng(-33.95961800162952, 18.459001667797565));
        LatLng[] points1 = {new LatLng(-33.95974536610191, 18.459064699709415), new LatLng(-33.95971116125141, 18.45920216292143), new LatLng(-33.959691973158534, 18.45903418958187), new LatLng(-33.95968307433137, 18.45921289175749), new LatLng(-33.95962078251523, 18.45899496227503), new LatLng(-33.95957211700214, 18.45925748348236), new LatLng(-33.959509825104746, 18.45901809632778), new LatLng(-33.95946394041902, 18.45930241048336), new LatLng(-33.959394140031456, 18.459043242037296), new LatLng(-33.959331848003735, 18.459347002208233), new LatLng(-33.959266775033235, 18.459059335291386), new LatLng(-33.95921644077832, 18.459391593933105), new LatLng(-33.9591305109594, 18.45942210406065), new LatLng(-33.95905181140507, 18.45920752733946), new LatLng(-33.959063769292584, 18.459491841495037), new LatLng(-33.95900286747592, 18.45931649208069), new LatLng(-33.95898673822037, 18.45965076237917), new LatLng(-33.95891999644076, 18.459363095462322), new LatLng(-33.95888745980422, 18.459689989686012), new LatLng(-33.958837125324884, 18.45941137522459), new LatLng(-33.958820717946736, 18.459725864231586), new LatLng(-33.958783731811494, 18.459441550076008), new LatLng(-33.95877177388463, 18.459739945828915), new LatLng(-33.95871393086584, 18.459486477077007), new LatLng(-33.958697801555516, 18.459784872829914), new LatLng(-33.95861910160092, 18.45953106880188), new LatLng(-33.958606031283395, 18.459684625267982), new LatLng(-33.95855708709778, 18.459566608071327), new LatLng(-33.95855708709778, 18.459630981087685)};
        engMall.setPolylines(mMap, points1);

        //West
        Parking west = new Parking("West", d, t[0]);
        west.setPolygon(mMap, new LatLng(-33.95773142781367, 18.45987405627966), new LatLng(-33.957432753535976, 18.459931053221226), new LatLng(-33.95747196502329, 18.46019323915243), new LatLng(-33.957924425346064, 18.460064828395844), new LatLng(-33.95803371626175, 18.460115790367126), new LatLng(-33.95865886872488, 18.45984287559986), new LatLng(-33.95833016368749, 18.459498547017574), new LatLng(-33.95774700111783, 18.45972117036581), new LatLng(-33.95776730202789, 18.459882773458958));
        LatLng[] points10 = {new LatLng(-33.95857321643478, 18.45975000411272), new LatLng(-33.958556809005735, 18.45988243818283), new LatLng(-33.95847393753608, 18.459643721580505), new LatLng(-33.95844501593035, 18.459931388497353), new LatLng(-33.958362700537165, 18.45952671021223), new LatLng(-33.95832793894519, 18.459984362125397), new LatLng(-33.958273710833325, 18.459518663585186), new LatLng(-33.958222541711464, 18.46002895385027), new LatLng(-33.95815663361241, 18.45956426113844), new LatLng(-33.958072371284835, 18.460098691284657), new LatLng(-33.9580061849756, 18.45962394028902), new LatLng(-33.957943891931656, 18.46006616950035), new LatLng(-33.957837381844584, 18.459686636924744), new LatLng(-33.95784906180843, 18.4600842744112), new LatLng(-33.95774616683375, 18.459872379899025), new LatLng(-33.95773810208723, 18.460113778710365), new LatLng(-33.95766969075844, 18.459887467324734), new LatLng(-33.95767803360636, 18.460134230554104), new LatLng(-33.95759682985183, 18.459896855056286), new LatLng(-33.95758292509158, 18.460160382092), new LatLng(-33.95749615933623, 18.459916301071644), new LatLng(-33.95746362215516, 18.460110425949097)};
        west.setPolylines(mMap, points10);
        parking.add(west);

        //SteveBiko
        Parking steveBiko = new Parking("Steve Biko", d, t[0]);
        steveBiko.setPolygon(mMap, new LatLng(-33.95654117399021, 18.459919653832912), new LatLng(-33.956559528498005, 18.460153676569462), new LatLng(-33.95650196207393, 18.46016339957714), new LatLng(-33.95651447651726, 18.460325337946415), new LatLng(-33.95682566841608, 18.460297510027885), new LatLng(-33.95683456754211, 18.46029046922922), new LatLng(-33.95683011797921, 18.460335060954094), new LatLng(-33.956555913216484, 18.460385017096996), new LatLng(-33.956555913216484, 18.46042189747095), new LatLng(-33.9565723210314, 18.460439667105675), new LatLng(-33.95658261067643, 18.460544273257256), new LatLng(-33.95644856709505, 18.460576459765434), new LatLng(-33.95643605264202, 18.4599669277668));
        LatLng[] points11 = {new LatLng(-33.956517535603126, 18.45992971211672), new LatLng(-33.956438277433804, 18.46006415784359), new LatLng(-33.95654645786407, 18.459990061819553), new LatLng(-33.95644078032449, 18.460175804793835), new LatLng(-33.95655619131506, 18.460103049874306), new LatLng(-33.95652587856402, 18.46015702933073), new LatLng(-33.956504743061494, 18.460195921361446), new LatLng(-33.95644217081929, 18.46023380756378), new LatLng(-33.956511139332555, 18.460283428430557), new LatLng(-33.95644467370986, 18.460335060954094), new LatLng(-33.95651558891214, 18.460324332118034), new LatLng(-33.95644522990774, 18.46040915697813), new LatLng(-33.95654033969431, 18.460325673222542), new LatLng(-33.956447176600356, 18.46048727631569), new LatLng(-33.956564256173614, 18.460322991013527), new LatLng(-33.956471371204714, 18.460570089519024), new LatLng(-33.95655535701931, 18.46042189747095), new LatLng(-33.9565244880706, 18.460557013750076), new LatLng(-33.956576492509264, 18.46048727631569), new LatLng(-33.956560084695155, 18.46054896712303), new LatLng(-33.956593734615595, 18.46031930297613), new LatLng(-33.956586782153785, 18.460378982126713), new LatLng(-33.95662154445718, 18.460315950214863), new LatLng(-33.95661375770245, 18.460374288260937), new LatLng(-33.95667994509488, 18.460310585796833), new LatLng(-33.956674939327506, 18.460362888872623), new LatLng(-33.956726109380135, 18.460306227207184), new LatLng(-33.95671721024275, 18.46035484224558), new LatLng(-33.956763652605694, 18.46030253916979), new LatLng(-33.95675809064739, 18.460347466170788), new LatLng(-33.95680842635674, 18.460299521684647), new LatLng(-33.9568036986947, 18.460340090095997), new LatLng(-33.95682066265728, 18.46029818058014), new LatLng(-33.95681593499591, 18.460337407886982)};
        steveBiko.setPolylines(mMap, points11);
        parking.add(steveBiko);

        //MolecularNorth
        Parking molNorth = new Parking("Molecular Biology North", d, t[0]);
        molNorth.setPolygon(mMap, new LatLng(-33.9551384329227, 18.460768572986126), new LatLng(-33.9551384329227, 18.460809476673603), new LatLng(-33.95484503352211, 18.460798747837543), new LatLng(-33.95482111655956, 18.46074879169464));
        LatLng[] points12 = {new LatLng(-33.955103391910036, 18.46076488494873), new LatLng(-33.95510227949671, 18.460805788636208), new LatLng(-33.95505694864087, 18.460761196911335), new LatLng(-33.95505778295133, 18.460805788636208), new LatLng(-33.955014398797175, 18.46075851470232), new LatLng(-33.955012452071784, 18.4608044475317), new LatLng(-33.954974351866014, 18.460754826664925), new LatLng(-33.95497129272466, 18.460803776979446), new LatLng(-33.95493792026644, 18.460754826664925), new LatLng(-33.95493513922766, 18.460799753665924), new LatLng(-33.95488758345051, 18.46075214445591), new LatLng(-33.95488758345051, 18.460798412561417), new LatLng(-33.95484837077217, 18.46075013279915), new LatLng(-33.95484837077217, 18.460797406733036)};
        molNorth.setPolylines(mMap, points12);
        parking.add(molNorth);

        //MolecularSouth
        Parking molSouth = new Parking("Molecular Biology South", d, t[0]);
        molSouth.setPolygon(mMap, new LatLng(-33.95645663196379, 18.460711240768433), new LatLng(-33.95651447651726, 18.460711240768433), new LatLng(-33.95651447651726, 18.460814841091633), new LatLng(-33.955689075512126, 18.460820205509663), new LatLng(-33.955689075512126, 18.4607595205307), new LatLng(-33.95645663196379, 18.460734374821186), new LatLng(-33.956465531128416, 18.460714928805828));
        LatLng[] points13 = {new LatLng(-33.95648944762876, 18.460710905492306), new LatLng(-33.95648499804781, 18.460813499987125), new LatLng(-33.95642882206832, 18.46073403954506), new LatLng(-33.95642687537529, 18.460815511643887), new LatLng(-33.956372924150934, 18.460736386477947), new LatLng(-33.95636958696067, 18.460814841091633), new LatLng(-33.95633176546191, 18.460739068686962), new LatLng(-33.95633287785917, 18.460814841091633), new LatLng(-33.956293943946335, 18.46073940396309), new LatLng(-33.95628949435516, 18.46081618219614), new LatLng(-33.95623943643844, 18.460740074515343), new LatLng(-33.95623415254551, 18.46081718802452), new LatLng(-33.956162680908925, 18.460743092000484), new LatLng(-33.95615767511111, 18.460817858576775), new LatLng(-33.95608759391088, 18.4607457742095), new LatLng(-33.956086481510404, 18.46081718802452), new LatLng(-33.95600805724067, 18.460749126970768), new LatLng(-33.95600750103993, 18.46081718802452), new LatLng(-33.95595577435404, 18.46074979752302), new LatLng(-33.95595799915837, 18.4608181938529), new LatLng(-33.95590738484521, 18.46075113862753), new LatLng(-33.95590738484521, 18.4608181938529), new LatLng(-33.95585565809846, 18.460752815008163), new LatLng(-33.95585677050194, 18.46081953495741), new LatLng(-33.95574497387879, 18.460758849978447), new LatLng(-33.95574302717012, 18.46081852912903), new LatLng(-33.955721613371814, 18.460759185254574), new LatLng(-33.955721613371814, 18.46081953495741), new LatLng(-33.95570131197365, 18.460759185254574), new LatLng(-33.95570019956812, 18.460818864405155)};
        molSouth.setPolylines(mMap, points13);
        parking.add(molSouth);

        //PD Hahn Bottom
        Parking pdBottom = new Parking("PD Hahn Bottom", d, t[1]);
        pdBottom.setPolygon(mMap, new LatLng(-33.95479914632058, 18.460678718984127), new LatLng(-33.95554696556885, 18.460730016231537), new LatLng(-33.95554919038387, 18.460719287395477), new LatLng(-33.95554084732721, 18.460702188313007), new LatLng(-33.95552026778394, 18.460684418678284), new LatLng(-33.955485505030616, 18.460667319595814), new LatLng(-33.955417091890595, 18.460620045661926), new LatLng(-33.95538232909512, 18.460620045661926), new LatLng(-33.955343116644784, 18.460629768669605), new LatLng(-33.955303904176375, 18.460629768669605), new LatLng(-33.95526469168989, 18.460629768669605), new LatLng(-33.95522520108239, 18.460615687072277), new LatLng(-33.95517347392093, 18.460602946579456), new LatLng(-33.955134817580785, 18.460587859153748), new LatLng(-33.955077250192794, 18.460558354854584), new LatLng(-33.95503553466991, 18.46052549779415), new LatLng(-33.95505555812345, 18.460525162518024), new LatLng(-33.95499632205974, 18.46051275730133), new LatLng(-33.954921512136345, 18.460505716502666), new LatLng(-33.95485782631328, 18.46049565821886), new LatLng(-33.954822785185065, 18.460505716502666), new LatLng(-33.95479775579894, 18.46053756773472), new LatLng(-33.95477550744953, 18.460560031235218));
        LatLng[] points14 = {new LatLng(-33.95551053421444, 18.460678718984127), new LatLng(-33.95550664078633, 18.460725657641888), new LatLng(-33.9554579729199, 18.46064753830433), new LatLng(-33.955447683138864, 18.460723310709), new LatLng(-33.95541792619752, 18.460619375109673), new LatLng(-33.955407358309294, 18.460720293223858), new LatLng(-33.9553728736123, 18.46062272787094), new LatLng(-33.95537509843187, 18.46071694046259), new LatLng(-33.95534756628545, 18.46062809228897), new LatLng(-33.955336164080784, 18.460715264081955), new LatLng(-33.95527998734279, 18.46062943339348), new LatLng(-33.95527331287641, 18.460711240768433), new LatLng(-33.955217692302845, 18.460613004863262), new LatLng(-33.9552140769643, 18.46070721745491), new LatLng(-33.95518821338412, 18.460606299340725), new LatLng(-33.95518821338412, 18.46070520579815), new LatLng(-33.955147332225195, 18.460592217743397), new LatLng(-33.9551359299937, 18.460702188313007), new LatLng(-33.95508086553714, 18.460559025406837), new LatLng(-33.95506890708985, 18.460697159171104), new LatLng(-33.95502190759467, 18.46051812171936), new LatLng(-33.955020238973056, 18.460693806409836), new LatLng(-33.95496517444163, 18.460510075092316), new LatLng(-33.95495460649718, 18.46068911254406), new LatLng(-33.954918731097024, 18.460505045950413), new LatLng(-33.954910387978536, 18.460686095058918), new LatLng(-33.95488090895335, 18.460497669875622), new LatLng(-33.954875624976054, 18.460684418678284), new LatLng(-33.95482306328932, 18.460504710674286), new LatLng(-33.954827234852935, 18.460680730640888), new LatLng(-33.954788300251145, 18.46054695546627), new LatLng(-33.954808601867164, 18.460678718984127)};
        pdBottom.setPolylines(mMap, points14);
        parking.add(pdBottom);

        //PD Hahn top
        Parking pdTop = new Parking("PD Hahn Bottom", d, t[1]);
        pdTop.setPolygon(mMap, new LatLng(-33.95606812690057, 18.460102379322052), new LatLng(-33.956070351701975, 18.4601741284132), new LatLng(-33.95595938966106, 18.460218720138073), new LatLng(-33.95573412792992, 18.460226766765118), new LatLng(-33.955728009701744, 18.460360877215862), new LatLng(-33.955583675009414, 18.460373282432556), new LatLng(-33.95556949181834, 18.46040278673172), new LatLng(-33.9554145889698, 18.4604125097394), new LatLng(-33.95540207436469, 18.460572436451912), new LatLng(-33.95531363776951, 18.46056640148163), new LatLng(-33.95524772741708, 18.46056640148163), new LatLng(-33.95514621981244, 18.460533544421196), new LatLng(-33.95505583622693, 18.46043698489666), new LatLng(-33.95497490807352, 18.460364565253258), new LatLng(-33.95486755995745, 18.46016775816679), new LatLng(-33.95486755995745, 18.46013557165861), new LatLng(-33.95491928730489, 18.46010372042656), new LatLng(-33.95539567801027, 18.46013020724058), new LatLng(-33.95562455595868, 18.460137248039246), new LatLng(-33.955721613371814, 18.46014730632305));
        LatLng[] points15 = {new LatLng(-33.95604254168024, 18.460106402635574), new LatLng(-33.956036423474245, 18.460184521973133), new LatLng(-33.95598998071435, 18.46011210232973), new LatLng(-33.95597551949053, 18.460212014615536), new LatLng(-33.95592407088584, 18.46011981368065), new LatLng(-33.955917674570664, 18.460220731794834), new LatLng(-33.95584369976005, 18.460130877792835), new LatLng(-33.955839250145324, 18.460223078727722), new LatLng(-33.955782517537266, 18.460137248039246), new LatLng(-33.95578057082947, 18.460225760936737), new LatLng(-33.95577473070577, 18.460139259696007), new LatLng(-33.95576026944536, 18.46022441983223), new LatLng(-33.9557152170414, 18.460145629942417), new LatLng(-33.95568796310645, 18.460364565253258), new LatLng(-33.95563484571833, 18.460137248039246), new LatLng(-33.955608704164355, 18.460370935499668), new LatLng(-33.955548077976374, 18.460135906934738), new LatLng(-33.95552026778394, 18.46040714532137), new LatLng(-33.95549217948035, 18.460132218897343), new LatLng(-33.955482724009734, 18.460408486425877), new LatLng(-33.95543266561836, 18.460134230554104), new LatLng(-33.95541375466286, 18.4604125097394), new LatLng(-33.95537259550985, 18.46013020724058), new LatLng(-33.95534367284987, 18.460566736757755), new LatLng(-33.95528582750045, 18.46012383699417), new LatLng(-33.955255236193985, 18.460566736757755), new LatLng(-33.95527525959582, 18.460123501718044), new LatLng(-33.95524744931422, 18.460564725100994), new LatLng(-33.955200171814646, 18.46011847257614), new LatLng(-33.95516902427115, 18.460541255772114), new LatLng(-33.955120912418174, 18.46011444926262), new LatLng(-33.95510700725328, 18.46049029380083), new LatLng(-33.95505889536525, 18.460110761225224), new LatLng(-33.955059729675675, 18.46043799072504), new LatLng(-33.95501161776089, 18.46010807901621), new LatLng(-33.955015511211656, 18.460398092865944), new LatLng(-33.95497268324347, 18.460105396807194), new LatLng(-33.95497379565851, 18.460362888872623), new LatLng(-33.954919843512755, 18.46010271459818), new LatLng(-33.95492095592849, 18.460260964930058), new LatLng(-33.954885914826264, 18.46012517809868), new LatLng(-33.954883968097946, 18.460194915533066)};
        pdTop.setPolylines(mMap, points15);
        parking.add(pdTop);

         /* Setup Icons on Map */

        //mMap.setOnMarkerDragListener(this); //For dragging for debugging


        try {

            Scanner iconReader = new Scanner(getActivity().getAssets().open("icons.txt"));

            //Post Office
            parseTxt(iconReader);
            Icon postOffice = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]); //Create new icon object
            postOffice.placeIcon(mMap,new LatLng(lat,lng)); //Place icon on map
            iconList.add(postOffice); //Add to array of icons

            //Juta Bookshop
            parseTxt(iconReader);
            Icon juta = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            juta.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(juta);

            //CPS
            parseTxt(iconReader);
            Icon cps = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            cps.placeIcon(mMap,new LatLng(lat, lng));
            iconList.add(cps);

            //Ridelink
            parseTxt(iconReader);
            Icon ridelink = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            ridelink.placeIcon(mMap,new LatLng(lat, lng));
            iconList.add(ridelink);


            //Info
            parseTxt(iconReader);
            Icon info = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            info.placeIcon(mMap,new LatLng(lat, lng));
            iconList.add(info);

            //Library
            parseTxt(iconReader);
            Icon lib = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            lib.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(lib);

            //Jammie Stop - North
            parseTxt(iconReader);
            Icon northStop = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            northStop.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(northStop);

            //Jammie Stop - West
            parseTxt(iconReader);
            Icon westStop = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            westStop.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(westStop);

            //Jammie Stop - South
            parseTxt(iconReader);
            Icon southStop = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            southStop.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(southStop);

            //Jammie Stop - Drop Off
            parseTxt(iconReader);
            Icon dropOff = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            dropOff.placeIcon (mMap, new LatLng(lat, lng));
            iconList.add(dropOff);

            //ATMS
            parseTxt(iconReader);
            Icon atmOB = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            atmOB.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(atmOB);

            parseTxt(iconReader);
            Icon atmLS = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            atmLS.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(atmLS);

            //Food court
            parseTxt(iconReader);
            Icon foodCourt = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            foodCourt.placeIcon(mMap,new LatLng(lat, lng) );
            iconList.add(foodCourt);

            //Frigo
            parseTxt(iconReader);
            Icon frigo = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            frigo.placeIcon(mMap,new LatLng(lat, lng) );
            iconList.add(frigo);

            //Supersandwich
            parseTxt(iconReader);
            Icon scienceFood = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            scienceFood.placeIcon(mMap,new LatLng(lat, lng) );
            iconList.add(scienceFood);

            //Food Humanities
            parseTxt(iconReader);
            Icon humFood = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            humFood.placeIcon(mMap, new LatLng(lat, lng) );
            iconList.add(humFood);

            //Engineering Food
            parseTxt(iconReader);
            Icon engineeringFood = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            engineeringFood.placeIcon(mMap, new LatLng(lat, lng ));
            iconList.add(engineeringFood);

            //Smuts Tuckshop
            parseTxt(iconReader);
            Icon smutsTuckshop = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            smutsTuckshop.placeIcon(mMap,new LatLng(lat, lng) );
            iconList.add(smutsTuckshop);

            //Sports Centre Pub
            parseTxt(iconReader);
            Icon sportsPub = new Icon(iconDetails[0],iconDetails[1], iconDetails[2]);
            sportsPub.placeIcon(mMap,new LatLng(lat, lng));
            iconList.add(sportsPub);

            //Food Leslie social
            parseTxt(iconReader);
            Icon foodLs = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            foodLs.placeIcon(mMap,new LatLng(lat, lng) );
            iconList.add(foodLs);

            //Standard parking by northstop (Blue) ///Crashing
            parseTxt(iconReader);
            Icon parkingNorth = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            parkingNorth.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(parkingNorth);

            //Standard parking by sports centre (Blue)
            parseTxt(iconReader);
            Icon parkingSportStan = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            parkingSportStan.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(parkingSportStan);

            //Standard parking green mile (Blue)
            parseTxt(iconReader);
            Icon parkingGreenMile = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            parkingGreenMile.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(parkingGreenMile);

            //Standard parking by southstop (Blue)
            parseTxt(iconReader);
            Icon parkingSouth = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            parkingSouth.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(parkingSouth);

            //Sport Centre Staff (Yellow)
            parseTxt(iconReader);
            Icon parkingSportStaff = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            parkingSportStaff.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(parkingSportStaff);

            //PD Hahn Parking (Yellow)
            parseTxt(iconReader);
            Icon postGradParkingPDH = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            postGradParkingPDH.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(postGradParkingPDH);

            //West Stop Parking (Yellow)
            parseTxt(iconReader);
            Icon westParking = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            westParking.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(westParking);

            //Nursery Rd Parking
            parseTxt(iconReader);
            Icon nurseryRdParking1 = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            nurseryRdParking1.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(nurseryRdParking1);

            //PD Hahn staff parking
            parseTxt(iconReader);
            Icon staffParkingPDH = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            staffParkingPDH.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(staffParkingPDH);

            //PD Hahn staff parking
            parseTxt(iconReader);
            Icon staffParkingHoeri = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            staffParkingHoeri.placeIcon(mMap, new LatLng(lat, lng));
            iconList.add(staffParkingHoeri);

            //PD Hahn staff parking
            parseTxt(iconReader);
            Icon staffParkingSnape = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            staffParkingSnape.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(staffParkingSnape);

            //PD Hahn staff parking
            parseTxt(iconReader);
            Icon staffParkingWest = new Icon (iconDetails[0],iconDetails[1], iconDetails[2]);
            staffParkingWest.placeIcon(mMap, new LatLng(lat,lng));
            iconList.add(staffParkingWest);

            iconReader.close(); //Close scanner when done reading

        }
        catch (IOException e){
            Toast.makeText(getActivity().getApplicationContext(), "Error",
                    Toast.LENGTH_SHORT).show();
        }


        started = true;

        return v;
    }
    /**
     * Parses text from icons.tx
     * Textfile format: iconName, iconType, iconDescription, latitude, longitude
     * @param s Scanner object to use
     */
    public void parseTxt(Scanner s){
        iconDetails = s.nextLine().split("~");
        coords = (iconDetails[3].split(","));
        lat = Double.parseDouble(coords[0]); //Get lat from txt
        lng =  Double.parseDouble(coords[1]); //Get long from txt
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        Toast.makeText(getActivity(), "Tap a building or point of interest for more information.",
                Toast.LENGTH_LONG).show();
        mapView.onResume();
        super.onResume();
        //Log.d("MOFUCK:", "Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /*
        Click Event for Building Polygons
     */
    public void onOverlayClick(LatLng click, final Building building) {
        int i;
        int j;
        List<LatLng> bounds=building.rectOptions.getPoints();
        boolean result = false;
        for (i = 0, j = bounds.size() - 1; i < bounds.size(); j = i++) {
            if ((bounds.get(i).longitude > click.longitude) != (bounds.get(j).longitude > click.longitude) &&
                    (click.latitude < (bounds.get(j).latitude - bounds.get(i).latitude) *
                            (click.longitude - bounds.get(i).longitude) / (bounds.get(j).longitude-bounds.get(i).longitude) + bounds.get(i).latitude)) {
                result = !result;

                //InformationByFaculty(building.faculty);

            }

        }
        if (result == true){
            Toast.makeText(getActivity().getApplicationContext(), building.name,
                    Toast.LENGTH_SHORT).show();


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker arg0) {

                    View v = getActivity().getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                    TextView textview = (TextView) v.findViewById(R.id.textView);
                    TextView textview1 = (TextView) v.findViewById(R.id.textView2);

                    textview.setText(building.name);
                    textview1.setText(building.faculty);

                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                    imageView.setImageResource(building.photo);



                    return v;

                }
            });



            if (oldMarker!=null)
                oldMarker.remove();
            Marker buildingMarker = mMap.addMarker(new MarkerOptions()
                    .position(click)
                    .alpha(0.0f)
                    .title(building.name)
                    .snippet("Faculty: " + building.faculty));

            buildingMarker.showInfoWindow();

            oldMarker = buildingMarker;

        }
    }


    @Override
    public void onMapClick(LatLng latLng) {

     //   System.out.println("new LatLng("+latLng.latitude+", "+latLng.longitude+"),");

        for (int m = 0; m<buildings.size(); m++)
        {

            onOverlayClick(latLng, buildings.get(m));
        }



    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {



        int m;

        for (m = 0; m<iconList.size(); m++)
        {
            if (iconList.get(m).name.equals(marker.getTitle()))
                break;

        }

        iconClick(iconList.get(m), marker);
        return false;
    }

    public void iconClick(final Icon icon, Marker marker)
    {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                TextView textview = (TextView) v.findViewById(R.id.textView);
                TextView textview1 = (TextView) v.findViewById(R.id.textView2);

                textview.setText(icon.name);
                textview1.setText(icon.description);

               // ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
               // imageView.setImageResource(R.drawable.maths);


                return v;

            }
        });

        marker.showInfoWindow();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.add("Menu 2").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:

        }

        return false;
    }

    public boolean isCurrentLocationUCT(LatLng currentLocation) {
        int i;
        int j;
        List<LatLng> bounds = uctBounds;
        boolean result = false;
        for (i = 0, j = bounds.size() - 1; i < bounds.size(); j = i++) {
            if ((bounds.get(i).longitude > currentLocation.longitude) != (bounds.get(j).longitude > currentLocation.longitude) &&
                    (currentLocation.latitude < (bounds.get(j).latitude - bounds.get(i).latitude) *
                            (currentLocation.longitude - bounds.get(i).longitude) / (bounds.get(j).longitude - bounds.get(i).longitude) + bounds.get(i).latitude)) {
                result = !result;

                //filterByFaculty(building.faculty);

            }

        }
        return result;
    }
}


