package mim.com.dc3scanner.scannerActivityFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mim.com.dc3scanner.R;
import mim.com.dc3scanner.util.interfaces.ListenerClick;
import mim.com.dc3scanner.util.adapters.SancionesAdapter;
import mim.com.dc3scanner.util.models.Sanciones;
import mim.com.dc3scanner.util.recycler.RecyclerViewEmpty;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SancionesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SancionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SancionesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerViewEmpty recycler;
    private RecyclerViewEmpty.Adapter adapter;
    private RecyclerViewEmpty.LayoutManager manager;


    //private RecyclerView recycler;
    //private LinearLayoutManager manager;
    private List<Sanciones> list;
    //private SancionesAdapter adapter;

    public SancionesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SancionesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SancionesFragment newInstance(String param1, String param2) {
        SancionesFragment fragment = new SancionesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sanciones, container, false);


        recycler = (RecyclerViewEmpty)v.findViewById(R.id.sanciones_recycler);
        recycler.setEmptyView(v.findViewById(R.id.empty_view_sancion));
        manager = new LinearLayoutManager(SancionesFragment.this.getContext());
        recycler.setLayoutManager(manager);

        list = new ArrayList<>();

        retrieveData();


        adapter = new SancionesAdapter(list, new ListenerClick() {
            @Override
            public void click(int position) {
                Log.d("POSICION", String.valueOf(position));
                //mListener.showPersonalList(list.get(position));

            }
        });

        recycler.setAdapter(adapter);
        return v;
    }

    private void retrieveData() {
        list = mListener.retrieveDataSanciones();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        List<Sanciones> retrieveDataSanciones();
    }
}
