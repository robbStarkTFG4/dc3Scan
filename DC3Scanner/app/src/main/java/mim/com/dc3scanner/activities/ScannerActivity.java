package mim.com.dc3scanner.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;

import mim.com.dc3scanner.R;
import mim.com.dc3scanner.scannerActivityFragments.ActiveJobsContratistasFragment;
import mim.com.dc3scanner.scannerActivityFragments.ActiveJobsFragment;
import mim.com.dc3scanner.scannerActivityFragments.CameraFragment;
import mim.com.dc3scanner.scannerActivityFragments.CameraFragmentProfilePicture;
import mim.com.dc3scanner.scannerActivityFragments.CertificationInfoFragment;
import mim.com.dc3scanner.scannerActivityFragments.CertificationListFragment;
import mim.com.dc3scanner.scannerActivityFragments.CompaniesListFragment;
import mim.com.dc3scanner.scannerActivityFragments.JobClosureFragment;
import mim.com.dc3scanner.scannerActivityFragments.JobCommentFragment;
import mim.com.dc3scanner.scannerActivityFragments.MapBodegaFragment;
import mim.com.dc3scanner.scannerActivityFragments.MapFragment;
import mim.com.dc3scanner.scannerActivityFragments.MapPTARFragment;
import mim.com.dc3scanner.scannerActivityFragments.PersonelCriticListFragment;
import mim.com.dc3scanner.scannerActivityFragments.PersonelListFragment;
import mim.com.dc3scanner.scannerActivityFragments.ProfileFragment;
import mim.com.dc3scanner.scannerActivityFragments.RegisterPermisionFragment;
import mim.com.dc3scanner.scannerActivityFragments.SancionesFragment;
import mim.com.dc3scanner.util.Tasks.CompresImages2;
import mim.com.dc3scanner.util.Tasks.Uploader2;
import mim.com.dc3scanner.util.interfaces.CanvasCommand;
import mim.com.dc3scanner.util.interfaces.CompresConsumer;
import mim.com.dc3scanner.util.interfaces.FragmentCommand;
import mim.com.dc3scanner.util.Tasks.CompresImages;
import mim.com.dc3scanner.util.Tasks.Uploader;
import mim.com.dc3scanner.util.models.Area;
import mim.com.dc3scanner.util.interfaces.ProfileLink;
import mim.com.dc3scanner.util.models.Empresa;
import mim.com.dc3scanner.util.models.Fotos;
import mim.com.dc3scanner.util.models.MapaCertificaciones;
import mim.com.dc3scanner.util.models.PermisoTrabajo;
import mim.com.dc3scanner.util.models.Sanciones;
import mim.com.dc3scanner.util.models.Subarea;
import mim.com.dc3scanner.util.models.Trabajador;
import mim.com.dc3scanner.util.models.Usuario;
import mim.com.dc3scanner.util.server.AreaAPI;
import mim.com.dc3scanner.util.server.CertificacionesAPI;
import mim.com.dc3scanner.util.server.EmpresasAPI;
import mim.com.dc3scanner.util.server.PermisoAPI;
import mim.com.dc3scanner.util.server.SancionesAPI;
import mim.com.dc3scanner.util.server.SubAreaAPI;
import mim.com.dc3scanner.util.server.TrabajadorAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentInteractionListener
        , CertificationListFragment.OnFragmentInteractionListener, CertificationInfoFragment.OnFragmentInteractionListener
        , CompaniesListFragment.OnFragmentInteractionListener, PersonelListFragment.OnFragmentInteractionListener
        , RegisterPermisionFragment.OnFragmentInteractionListener
        , CameraFragment.PhotosConsumer, CompresConsumer, Uploader.UploaderConsumer, SancionesFragment.OnFragmentInteractionListener
        , MapFragment.OnFragmentInteractionListener, ActiveJobsFragment.OnFragmentInteractionListener
        , JobClosureFragment.OnFragmentInteractionListener
        , PersonelCriticListFragment.OnFragmentInteractionListener
        , CameraFragmentProfilePicture.PhotosConsumer
        , Uploader2.UploaderConsumerProfile
        , ActiveJobsContratistasFragment.OnFragmentInteractionListener
        , JobCommentFragment.OnFragmentInteractionListener
        , MapPTARFragment.OnFragmentInteractionListener
        , MapBodegaFragment.OnFragmentInteractionListener {

    private FragmentManager manager;

    private Trabajador trabajador;
    private List<MapaCertificaciones> certificatesList;
    private List<Fotos> fotos;
    private List<Fotos> fotosProfile;
    private List<Empresa> empresasList;
    private List<Trabajador> workerList;

    private List<Trabajador> workerListCritics;


    private ProfileLink link;
    private FragmentCommand command;
    private FragmentCommand command2;
    private ProgressDialog pg;

    //current permiso
    private int idCurrentOrden = 0;
    private List<Sanciones> sancionesList;
    private FloatingActionButton fab;
    private com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton rightLowerButton;
    private CanvasCommand canvasCommand;
    private int parentHeight = 0;

    private String marker;
    private List<PermisoTrabajo> trabajosList;
    private Usuario currentUser;
    private SubActionButton b1;
    private SubActionButton b2;
    private SubActionButton b3;
    private SubActionButton b4;
    private SubActionButton b5;
    private int posCommentOrder;

    private int mapaselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        currentUser = getIntent().getParcelableExtra("parce");
        Toast.makeText(ScannerActivity.this, currentUser.getNombre(), Toast.LENGTH_LONG).show();

        pg = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(ScannerActivity.this, CertificationActivity.class);
                startActivity(intent);*/
                if (trabajador == null) {
                    Snackbar.make(view, "Escanea codigo", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (manager == null) {
                    manager = getSupportFragmentManager();
                }
                pg.setMessage("Cargando datos..");
                pg.show();
                CertificacionesAPI.Factory.getInstance().findCertificates(trabajador.getIdtrabajador()).enqueue(new Callback<List<MapaCertificaciones>>() {
                    @Override
                    public void onResponse(Call<List<MapaCertificaciones>> call, Response<List<MapaCertificaciones>> response) {
                        pg.dismiss();
                        certificatesList = response.body();
                        CertificationListFragment fragment = CertificationListFragment.newInstance(null, null);
                        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
                    }

                    @Override
                    public void onFailure(Call<List<MapaCertificaciones>> call, Throwable t) {
                        pg.dismiss();
                        Toast.makeText(ScannerActivity.this, t.getCause().toString() + " id: " + trabajador.getIdtrabajador(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        verifyManager();
        ProfileFragment fragment = ProfileFragment.newInstance(null, null);
        link = fragment;
        manager.beginTransaction().replace(R.id.content, fragment).commit();
        buildRadialMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FrameLayout mainLayout = findViewById(R.id.content);
        mainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.d("ALTURA LAYOUT PAR!", String.valueOf(i3 - i1));
                parentHeight = i3 - i1;
                mainLayout.removeOnLayoutChangeListener(this);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void buildRadialMenu() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.active));
        rightLowerButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        rightLowerButton.setVisibility(View.GONE);

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);

        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);
        ImageView rlIcon5 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.back));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.next));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.down_arrow));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.up_arrow));
        rlIcon5.setImageDrawable(getResources().getDrawable(R.drawable.in_activve));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        b1 = rLSubBuilder.setContentView(rlIcon1).build();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasCommand.moveLeft();
            }
        });

        b2 = rLSubBuilder.setContentView(rlIcon2).build();
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasCommand.moveRight();
            }
        });

        b3 = rLSubBuilder.setContentView(rlIcon3).build();
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasCommand.moveBottom();
            }
        });

        b4 = rLSubBuilder.setContentView(rlIcon4).build();
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasCommand.moveTop();
            }
        });

        b5 = rLSubBuilder.setContentView(rlIcon5).build();
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasCommand.placeMarker();
            }
        });

        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(b1)
                .addSubActionView(b2)
                .addSubActionView(b3)
                .addSubActionView(b4)
                .addSubActionView(b5)

                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //fab.setVisibility(View.VISIBLE);
        rightLowerButton.setVisibility(View.GONE);
        //if (rightLowerButton.isActivated()) {
        rightLowerButton.setActivated(false);
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void barcodeReader() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this, "evalua resultado", Toast.LENGTH_LONG).show();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String toast;
        switch (requestCode) {
            case CameraFragment.REQUEST_IMAGE_CAPTURE:
                //Toast.makeText(this, "evalua resultado", Toast.LENGTH_LONG).show();
                if (command != null) {
                    command.executeCommand(resultCode);
                }
                break;
            default:
                if (result != null) {
                    if (result.getContents() == null) {
                        toast = "Cancelado";
                    } else {
                        toast = "Codigo: " + result.getContents();

                        String res = result.getContents();
                       /* Puesto puesto = new Puesto();
                        puesto.setNombrePuesto("Soldador");
                        trabajador = new Trabajador("Marco Isaac Vazquez Gutierrez", "oficial electricista", "13123dads1231213", Integer.parseInt("27"), "9932772169");
                        trabajador.setPuestoIdpuesto(puesto);*/
                        TrabajadorAPI.Factory.getInstance().findByCode(res).enqueue(new Callback<Trabajador>() {
                                                                                        @Override
                                                                                        public void onResponse(Call<Trabajador> call, Response<Trabajador> response) {
                                                                                            trabajador = response.body();
                                                                                            if (link != null) {
                                                                                                //Toast.makeText(ScannerActivity.this, "deberia actualizarse", Toast.LENGTH_LONG).show();
                                                                                                link.sync();
                                                                                            } else {
                                                                                                Toast.makeText(ScannerActivity.this, "link es nulo", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailure(Call<Trabajador> call, Throwable t) {
                                                                                            Toast.makeText(ScannerActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }
                        );

                    }
                    //Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fab.setVisibility(View.GONE);
        rightLowerButton.setVisibility(View.GONE);
        if (id == R.id.nav_camera) {
            rightLowerButton.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            barcodeReader();
        } else if (id == R.id.nav_gallery) {
            rightLowerButton.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            companiesList();

        } else if (id == R.id.nav_slideshow) {
            if (currentUser.getAfiliacion() == null) {
                permisosList();
            } else {
                contratistasPermisosList();

            }
        } else if (id == R.id.nav_share) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://node37201-dc3-2018.jl.serv.net.mx/dc3BackEnd2/webresources/privacy"));
            startActivity(browserIntent);
            //launchMap();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void contratistasPermisosList() {
        pg.setMessage("Cargando datos..");
        pg.show();
        PermisoAPI.Factory.getInstance().retrieveOpenByCompany(currentUser.getAfiliacion()).enqueue(new Callback<List<PermisoTrabajo>>() {
            @Override
            public void onResponse(Call<List<PermisoTrabajo>> call, Response<List<PermisoTrabajo>> response) {
                trabajosList = response.body();
                pg.dismiss();
                //Toast.makeText(ScannerActivity.this, empresasList.get(0).getNombre(), Toast.LENGTH_LONG).show();
                verifyManager();
                ActiveJobsContratistasFragment fragment = ActiveJobsContratistasFragment.newInstance(null, null);

                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<List<PermisoTrabajo>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void permisosList() {
        pg.setMessage("Cargando datos..");
        pg.show();
        PermisoAPI.Factory.getInstance().retrieveOpen().enqueue(new Callback<List<PermisoTrabajo>>() {
            @Override
            public void onResponse(Call<List<PermisoTrabajo>> call, Response<List<PermisoTrabajo>> response) {
                pg.dismiss();
                trabajosList = response.body();
                //Toast.makeText(ScannerActivity.this, empresasList.get(0).getNombre(), Toast.LENGTH_LONG).show();
                verifyManager();
                ActiveJobsFragment fragment = ActiveJobsFragment.newInstance(null, null);

                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<List<PermisoTrabajo>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void launchMap() {
        fab.setVisibility(View.GONE);
        rightLowerButton.setVisibility(View.VISIBLE);
        MapFragment fragment = MapFragment.newInstance(null, null);

        manager.beginTransaction().replace(R.id.content, fragment, "mapita").addToBackStack(null).commit();
    }

    @Override
    public void launchMapPTAR() {
        fab.setVisibility(View.GONE);
        rightLowerButton.setVisibility(View.VISIBLE);
        MapPTARFragment fragment = MapPTARFragment.newInstance(null, null);

        manager.beginTransaction().replace(R.id.content, fragment, "mapita2").addToBackStack(null).commit();
    }

    @Override
    public void launchMapBodega() {
        fab.setVisibility(View.GONE);
        rightLowerButton.setVisibility(View.VISIBLE);
        MapBodegaFragment fragment = MapBodegaFragment.newInstance(null, null);

        manager.beginTransaction().replace(R.id.content, fragment, "mapita2").addToBackStack(null).commit();
    }

    private void companiesList() {
        pg.setMessage("Cargando datos..");
        pg.show();
        EmpresasAPI.Factory.getInstance().findAll().enqueue(new Callback<List<Empresa>>() {
            @Override
            public void onResponse(Call<List<Empresa>> call, Response<List<Empresa>> response) {
                pg.dismiss();
                empresasList = response.body();
                if (currentUser.getAfiliacion() != null) {
                    prepareData(empresasList);
                }
                //Toast.makeText(ScannerActivity.this, empresasList.get(0).getNombre(), Toast.LENGTH_LONG).show();
                verifyManager();
                CompaniesListFragment fragment = CompaniesListFragment.newInstance(null, null);

                manager.beginTransaction().replace(R.id.content, fragment, "MY_FRAGMENT").addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<List<Empresa>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, "hubo algun error", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void prepareData(List<Empresa> empresasList) {
        Empresa target = null;
        for (Empresa em : empresasList) {
            if (currentUser.getAfiliacion().equals(em.getNombre())) {
                target = em;
            }
        }
        empresasList.clear();
        empresasList.add(target);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showPersonDataCritics(Trabajador trabajador) {
        certificatesList = trabajador.getMapaCertificacionesList();
        if (certificatesList != null) {
            CertificationListFragment fragment = CertificationListFragment.newInstance(null, null);
            manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
        }

    }

    @Override
    public List<Trabajador> retrieveWorkerListCritics() {
        return workerListCritics;
    }

    @Override
    public void sendClosure(String comentario) {
        pg.setMessage("Cerrando actividad...");
        pg.show();
        PermisoAPI service = PermisoAPI.Factory.getInstance();
        PermisoTrabajo res = new PermisoTrabajo();
        res.setIdpermisoTrabajo(idCurrentOrden);
        res.setComentario(comentario);
        service.closeJob(idCurrentOrden, res).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, "Actividad cerrada: " + response.code(), Toast.LENGTH_SHORT).show();
                Log.d("RESULTADO_COMPRESION: ", "funciono");
                closeService();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (ScannerActivity.this != null) {
                    pg.dismiss();
                    Toast.makeText(ScannerActivity.this, "hubo algun error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public PermisoTrabajo retrievePermiso() {
        return trabajosList.get(posCommentOrder);
    }

    @Override
    public void updateComment(PermisoTrabajo permiso, String comment) {
        pg.setMessage("Actualizando comentario...");
        pg.show();
        permiso.setComentario2(comment);
        PermisoAPI service = PermisoAPI.Factory.getInstance();
        service.updateComment(permiso.getIdpermisoTrabajo(), permiso).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                pg.dismiss();
                //Toast.makeText(ScannerActivity.this, "Actividad cerrada: " + response.code(), Toast.LENGTH_SHORT).show();
                Log.d("RESULTADO_COMPRESION: ", "funciono");
                closeService();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (ScannerActivity.this != null) {
                    pg.dismiss();
                    Toast.makeText(ScannerActivity.this, "hubo algun error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public List<PermisoTrabajo> retriveJobs() {
        return trabajosList;
    }

    @Override
    public void launchComment(int pos) {
        posCommentOrder = pos;
        verifyManager();
        JobCommentFragment fragment = JobCommentFragment.newInstance(null, null);

        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void launchClosureFragment(int pos) {
        idCurrentOrden = pos;
        verifyManager();
        JobClosureFragment fragment = JobClosureFragment.newInstance(null, null);

        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void linkCanvas(CanvasCommand canvas) {
        this.canvasCommand = canvas;
        canvasCommand.setParentHeight(parentHeight);
    }

    @Override
    public void saveMarker(String json) {
        marker = json;
    }

    @Override
    public void hideBotoncitos() {
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
        b4.setVisibility(View.GONE);
        b5.setVisibility(View.GONE);
        rightLowerButton.setVisibility(View.GONE);

    }

    @Override
    public void showBotoncitos() {
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
        b4.setVisibility(View.VISIBLE);
        b5.setVisibility(View.VISIBLE);
        rightLowerButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPermisoForm() {
        manager.popBackStackImmediate();
    }

    @Override
    public List<Sanciones> retrieveDataSanciones() {
        return sancionesList;
    }

    @Override
    public void photo() {
        verifyManager();
        CameraFragment fragment = CameraFragment.newInstance(null, null);
        command = fragment;

        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void uploadPermisoData(PermisoTrabajo permiso) {
        if (fotos != null) {
            if (fotos.size() > 0) {
                if (marker != null) {
                    permiso.setTrabajadorIdtrabajador(trabajador);
                    permiso.setPosicion(marker);
                    permiso.setMapa(mapaselected);
                    permiso.setUsuarioIdusuario(currentUser);
                    pg.setMessage("Subiendo informacion permiso....");
                    pg.show();
                    PermisoAPI.Factory.getInstance().persistPermiso(permiso).enqueue(new Callback<PermisoTrabajo>() {
                        @Override
                        public void onResponse(Call<PermisoTrabajo> call, Response<PermisoTrabajo> response) {
                            pg.setMessage("procesando imagenes....");
                            idCurrentOrden = response.body().getIdpermisoTrabajo();
                            uploadFotosObjects();
                        }

                        @Override
                        public void onFailure(Call<PermisoTrabajo> call, Throwable t) {
                            Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(ScannerActivity.this, "marca posicion en mapa.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ScannerActivity.this, "agrega foto de permiso", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ScannerActivity.this, "agrega foto de permiso", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void findAllAreas() {
        pg.setMessage("Cargando datos..");
        pg.show();

        AreaAPI.Factory.getInstance().findAll().enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {
                pg.dismiss();
                command2.executeDialog(response.body());
            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void findSubAreas(int id) {
        pg.setMessage("Cargando datos..");
        pg.show();
        SubAreaAPI.Factory.getInstance().findSubArea(id).enqueue(new Callback<List<Subarea>>() {
            @Override
            public void onResponse(Call<List<Subarea>> call, Response<List<Subarea>> response) {
                pg.dismiss();
                command2.executeDialogSubArea(response.body());
            }

            @Override
            public void onFailure(Call<List<Subarea>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFotosObjects() {
        PermisoAPI.Factory.getInstance().persistPhotoObjects(idCurrentOrden, fotos).enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                fileUpload(fotos);
                //Toast.makeText(ScannerActivity.this, "comprimiendo imagenes", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Fotos> call, Throwable t) {
                Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyManager() {
        if (manager == null) {
            manager = getSupportFragmentManager();
        }

        manager = getSupportFragmentManager();
    }

    @Override
    public void createPermiso(Trabajador trabajador) {
        if (currentUser.getAfiliacion() == null) {
            verifyManager();
            RegisterPermisionFragment fragment = RegisterPermisionFragment.newInstance(trabajador, null);
            command2 = fragment;
            manager.beginTransaction().replace(R.id.content, fragment, "permisoForm").addToBackStack(null).commit();
        } else {
            Toast.makeText(this, "Accion no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPersonData(Trabajador trabajador) {
        // CertificacionesAPI.Factory.getInstance().findCertificates(trabajador.getIdtrabajador())

        //Toast.makeText(this, "cambia de trabajador: " + trabajador.getNombreCompleto(), Toast.LENGTH_SHORT).show();
        this.trabajador = trabajador;
        verifyManager();
        ProfileFragment fragment = ProfileFragment.newInstance(trabajador, null);
        //fragment.sync();
        link = fragment;
        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void showCritics(Empresa company) {
        pg.setMessage("Cargando datos..");
        pg.show();
        TrabajadorAPI.Factory.getInstance().findCriticsCertificates(company.getIdempresa()).enqueue(new Callback<List<Trabajador>>() {
            @Override
            public void onResponse(Call<List<Trabajador>> call, Response<List<Trabajador>> response) {
                pg.dismiss();
                workerListCritics = response.body();
                verifyManager();
                PersonelCriticListFragment fragment = PersonelCriticListFragment.newInstance(null, null);

                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<List<Trabajador>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public List<Trabajador> retrieveWorkerList() {
        return workerList;
    }

    @Override
    public void showPersonalList(Empresa company) {
        pg.setMessage("Cargando datos..");
        pg.show();
        TrabajadorAPI.Factory.getInstance().findByCompany(company.getIdempresa()).enqueue(new Callback<List<Trabajador>>() {
            @Override
            public void onResponse(Call<List<Trabajador>> call, Response<List<Trabajador>> response) {
                pg.dismiss();
                workerList = response.body();
                verifyManager();
                PersonelListFragment fragment = PersonelListFragment.newInstance(null, null);

                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<List<Trabajador>> call, Throwable t) {
                pg.dismiss();
                Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public List<Empresa> retrieveData() {
        return empresasList;
    }

    @Override
    public void hideFloatingButtons() {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void setSelectedMap(int mapa) {
        mapaselected = mapa;
    }

    @Override
    public void closeKeyboard() {
        hideKeyboard(this);
    }

    @Override
    public void showCertificationInfo(MapaCertificaciones certificacion) {
        if (manager == null) {
            manager = getSupportFragmentManager();
        }
        CertificationInfoFragment fragment = CertificationInfoFragment.newInstance(certificacion, null);
        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    //solo comprime las imagenes
    private void fileUpload(List<Fotos> fotos) {
        // this.idCurrentOrden = idOrden;
        if (fotos != null) {
            if (fotos.size() > 0) {
                CompresImages task = new CompresImages(this, 0, this);
                Fotos[] array = new Fotos[fotos.size()];
                for (int i = 0; i < fotos.size(); i++) {
                    array[i] = fotos.get(i);
                }
                task.execute(array);
            }
        }
    }

    private void sendToServer() {

        if (fotos != null) {
            if (fotos.size() > 0) {
                pg.setMessage("Subiendo imagenes...");
                Uploader task = new Uploader(this, 0);
                Fotos[] array = new Fotos[fotos.size()];
                for (int i = 0; i < fotos.size(); i++) {
                    array[i] = fotos.get(i);
                }
                task.execute(array);
            }
        }
    }

    @Override
    public List<MapaCertificaciones> retriveData() {
        return certificatesList;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    @Override
    public void showSanciones(Integer idtrabajador) {
        pg.setMessage("Cargando datos..");
        pg.show();
        if (currentUser.getAfiliacion() == null) {

            SancionesAPI.Factory.getInstance().findSanciones(idtrabajador).enqueue(new Callback<List<Sanciones>>() {
                @Override
                public void onResponse(Call<List<Sanciones>> call, Response<List<Sanciones>> response) {
                    pg.dismiss();
                    sancionesList = response.body();
                    SancionesFragment fragment = SancionesFragment.newInstance(null, null);
                    manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
                }

                @Override
                public void onFailure(Call<List<Sanciones>> call, Throwable t) {
                    pg.dismiss();
                    Toast.makeText(ScannerActivity.this, t.getCause().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("RESULTADO_COMPRESION: ", "fallo");
                }
            });
        } else {
            Toast.makeText(this, "Accion no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void launchProfilePictureCaptureFragment(Trabajador trabajador) {
        //if (trabajador.getImagen() == null) {
        if (fotosProfile != null) {
            fotosProfile.clear();
        }
        verifyManager();
        CameraFragmentProfilePicture fragment = CameraFragmentProfilePicture.newInstance(null, null);
        command = fragment;
        manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
        //}
    }

    @Override
    public void showFloatingButton() {
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFloatingButton() {
        fab.setVisibility(View.GONE);
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    @Override
    public void setPhotosList(List<Fotos> list) {
        this.fotos = list;
    }

    @Override
    public List<Fotos> getPhotosList() {
        return fotos;
    }

    @Override
    public void startUploadProfilePic(List<Fotos> list) {
        profilePicUpload(list);
    }

    private void profilePicUpload(List<Fotos> list) {
        pg.show();
        pg.setMessage("comprimiendo...");
        fotosProfile = list;
        if (list != null) {
            if (list.size() > 0) {
                CompresImages2 task = new CompresImages2(this, 0, this);
                Fotos[] array = new Fotos[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    array[i] = list.get(i);
                }
                task.execute(array);
            }
        }
    }


    @Override
    public void compresResult(boolean res, int codigo) {
        if (res) {
            sendToServer();
        } else {
            Toast.makeText(this, "Fallo en compresion", Toast.LENGTH_SHORT).show();
            Log.d("RESULTADO_COMPRESION: ", "fallo");
        }
    }

    @Override
    public void compresResultProfilePic(boolean res, int codigo) {
        if (res) {
            sendToServer2();
        } else {
            Toast.makeText(this, "Fallo en compresion", Toast.LENGTH_SHORT).show();
            Log.d("RESULTADO_COMPRESION: ", "fallo");
        }
    }

    private void sendToServer2() {
        if (fotosProfile != null) {
            if (fotosProfile.size() > 0) {

                pg.setMessage("Subiendo imagen...");
                Uploader2 task = new Uploader2(this, 0, String.valueOf(trabajador.getIdtrabajador()));
                Fotos[] array = new Fotos[fotosProfile.size()];
                for (int i = 0; i < fotosProfile.size(); i++) {
                    array[i] = fotosProfile.get(i);
                }
                task.execute(array);
            }
        }
    }

    private void closeService() {
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.putExtra("parce", currentUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void consumeUpload(boolean res, int codigo) {

        if (res) {
            PermisoAPI service = PermisoAPI.Factory.getInstance();
            service.markOrder(idCurrentOrden).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    pg.dismiss();
                    Toast.makeText(ScannerActivity.this, "Reporte procesado: " + response.code(), Toast.LENGTH_SHORT).show();
                    closeService();
                    // Toast.makeText(DataActivity.this, "Subiendo imagenes...", Toast.LENGTH_SHORT).show();
                    //sendToServer(DataActivity.this.idCurrentOrden);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    if (ScannerActivity.this != null) {
                        Toast.makeText(ScannerActivity.this, throwable.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            pg.dismiss();
            Toast.makeText(ScannerActivity.this, "Falla al subir info. ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void consumeProfilePicUpload(boolean res, int codigo) {
        if (res) {
            pg.dismiss();
            manager.popBackStackImmediate();
            Toast.makeText(this, "foto perfil actualizada..", Toast.LENGTH_SHORT).show();
            link.updateProfilePic();
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
