package mim.com.dc3scanner.scannerActivityFragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mim.com.dc3scanner.R;
import mim.com.dc3scanner.util.interfaces.FragmentCommand;
import mim.com.dc3scanner.util.Modifier;
import mim.com.dc3scanner.util.interfaces.Navigator;
import mim.com.dc3scanner.util.adapters.FotosAdapter;
import mim.com.dc3scanner.util.models.Area;
import mim.com.dc3scanner.util.models.Fotos;
import mim.com.dc3scanner.util.models.Subarea;
import mim.com.dc3scanner.util.recycler.RecyclerViewEmpty;
import mim.com.dc3scanner.util.security.Permission;


public class CameraFragment extends Fragment implements FotosAdapter.PositionConsumer, FragmentCommand {
    private final int SELECT_PHOTO = 199;
    private final int SELECT_PHOTO_MULTI = 149;
    private boolean control = false;

    @Override
    public void position(int position) {
        ImageViewFragment dialog = ImageViewFragment.newInstance(dataList.get(position).getArchivo(), null);
        dialog.show(getFragmentManager(), "imgDialog");
    }

    @Override
    public void executeCommand(int resultCode) {
        imageResult(resultCode);
    }

    @Override
    public void executeDialog(List<Area> list) {

    }

    @Override
    public void executeDialogSubArea(List<Subarea> list) {

    }

    @Override
    public List<Fotos> getUpdatedList() {
        return dataList;
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public interface PhotosConsumer {
        public void setPhotosList(List<Fotos> list);

        public List<Fotos> getPhotosList();

        public void showPermisoForm();
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Navigator navigator;

    private RecyclerViewEmpty mRecyclerView;
    private RecyclerViewEmpty.Adapter mAdapter;
    private RecyclerViewEmpty.LayoutManager mLayoutManager;

    private List<Fotos> dataList = new ArrayList<>();
    private FloatingActionButton floatButton;
    private String ruta;

    private PhotosConsumer consumer;

    public static final int REQUEST_IMAGE_CAPTURE = 24;
    public static final int REQUEST_CAMERA_RESULT = 19123;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // if (context instanceof Navigator) {
        //navigator = (Navigator) context;
        consumer = (PhotosConsumer) context;
        //}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        dataList = consumer.getPhotosList();
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        widgetSetUp(view);
        dataSetUp();
        recyclerSetUp(view);

        InputMethodManager inputManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:

        inputManager.hideSoftInputFromWindow(container.getWindowToken(), 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getContext(), "No Permission to use the Camera services", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAMERA_RESULT);

            }
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (consumer != null) {
            consumer.setPhotosList(dataList);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_camera, menu);
        Modifier.changeMenuItemColor(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_foto:
                try {
                    //Log.v("CAMERA", mCameraId + " " + mCameraDeviceStateCallback);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            cameraIntent();
                        } else {
                            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                                Toast.makeText(getContext(), "No Permission to use the Camera services", Toast.LENGTH_SHORT).show();
                            }
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);

                        }
                    } else {
                        cameraIntent();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

                break;
            case R.id.camera_previous:
                consumer.showPermisoForm();
                break;
            case R.id.camera_foto_multi:
                pickMultiple();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickPhoto() {
        control = false;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    private void pickMultiple() {
        control = false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO_MULTI);

    }

    private void dataSetUp() {
        if (consumer.getPhotosList() != null) {
            dataList = consumer.getPhotosList();
        }
    }

    private void widgetSetUp(View view) {
        floatButton = (FloatingActionButton) view.findViewById(R.id.fab_picture);
        floatButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (Permission.verifyStorage(getActivity(), Manifest.permission.CAMERA)) {
                        cameraIntent();
                    } else {
                        Permission.verifyStoragePermissions(getActivity());
                    }
                } else {
                    cameraIntent();
                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Permission.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "no tendras acceso a estas funciones hasta que aceptes los permisos", Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }
        }
    }

    private File createImageFile() throws IOException {

        Random r = new Random();

        int i1 = r.nextInt(200 - 70) + 17;
        int i2 = r.nextInt(67 - 12) - 6;
        int i3 = r.nextInt(89 - 27) - 6;
        int i4 = r.nextInt(200 - 70) + 17;
        int i5 = r.nextInt(89 - 27) - 6;
        String photoTitle = "JPEG_" + i1 + i2 + "uncompressed_" + i3 + i4 + "-" + i5;

        File image = new File(Environment.  //THIS WORKS
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                photoTitle + ".jpg");

        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                ruta = photoFile.getPath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
                Toast.makeText(getContext(), "exception in file creation", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Uri.fromFile(photoFile)
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(CameraFragment.this.getContext(), CameraFragment.this.getContext().getApplicationContext().getPackageName() + ".my.package.name.provider", photoFile);
                } else {
                    uri = Uri.fromFile(photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        uri);
                Toast.makeText(getContext(), "INICIANDO CAPTURA DE IMAGEN", Toast.LENGTH_LONG).show();
                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(getContext(), "no se creo el archivo temporal", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "package manager es nulo", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getContext(), "EVALUAR RESULTADO", Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                imageResult(resultCode);
                break;
            case SELECT_PHOTO:
                if (resultCode == ((Activity) getContext()).RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final File imageFile = proccessImageFile(imageUri);


                        FotoDialogFragment foto = FotoDialogFragment.newInstance(null, null, 0);
                        foto.show(getFragmentManager(), "dialog");
                        ruta = imageFile.getPath();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case SELECT_PHOTO_MULTI:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            try {
                                final File imageFile = proccessImageFile(uri);
                                ruta = imageFile.getPath();
                                setPhotoInfo("modificar", "modificar", false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(this.getContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                break;
        }
    }

    private File proccessImageFile(Uri imageUri) throws IOException {
        final InputStream imageStream = ((Activity) getContext()).getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);


        final File imageFile = createImageFile();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OutputStream stream = new FileOutputStream(imageFile);
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyChange();
            }
        }.execute();
        return imageFile;
    }

    private void notifyChange() {
        mAdapter.notifyDataSetChanged();
    }

    private void imageResult(int resultCode) {
        //Toast.makeText(getContext(), "EVALUAR RESULTADO", Toast.LENGTH_LONG).show();
        if (resultCode == ((Activity) getContext()).RESULT_OK) {
            Log.d("CAMARA", "OPEN DIALOG");
            control = true;
            //FotoDialogFragment foto = FotoDialogFragment.newInstance(null, null, 0);
            //foto.show(getFragmentManager(), "dialog");
            setPhotoInfo("default", "dafault");

        } else if (resultCode == ((Activity) getContext()).RESULT_CANCELED) {
            // User cancelled the image capture
            if (ruta != null) { //  HAY QUE BORRAR EL ARCHIVO!!!!!!!!!
                // Toast.makeText(getContext(), "borra archivo", Toast.LENGTH_LONG).show();
            }
        } else {
            // Image capture failed, advise user
        }
    }

    private void recyclerSetUp(View view) {
        mRecyclerView = (RecyclerViewEmpty) view.findViewById(R.id.fotos_reciclador);
        mRecyclerView.setEmptyView((TextView) view.findViewById(R.id.empty_view));
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FotosAdapter(dataList, getActivity(), this);

        mRecyclerView.setAdapter(mAdapter);

    }

    /*public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }*/

    public void setPhotoInfo(String title, String descripcion) {
        Fotos current = new Fotos();
        current.setArchivo(ruta);
        current.setTitulo(title);
        current.setDescripcion(descripcion);
        dataList.add(current);
        //if (control) {
        mAdapter.notifyDataSetChanged();
        //}
    }

    public void setPhotoInfo(String title, String descripcion, boolean call) {
        Fotos current = new Fotos();
        current.setArchivo(ruta);
        current.setTitulo(title);
        current.setDescripcion(descripcion);
        dataList.add(current);
        //if (control) {

        //}
    }
}
