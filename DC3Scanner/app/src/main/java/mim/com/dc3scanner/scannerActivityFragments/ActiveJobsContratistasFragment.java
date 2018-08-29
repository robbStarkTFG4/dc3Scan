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

import java.util.List;

import mim.com.dc3scanner.R;
import mim.com.dc3scanner.util.adapters.PermisosAdapter;
import mim.com.dc3scanner.util.adapters.PermisosContratistasAdapter;
import mim.com.dc3scanner.util.interfaces.ListenerClick;
import mim.com.dc3scanner.util.models.PermisoTrabajo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveJobsContratistasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveJobsContratistasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveJobsContratistasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private List<PermisoTrabajo> list;
    private PermisosContratistasAdapter adapter;


    public ActiveJobsContratistasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveJobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActiveJobsContratistasFragment newInstance(String param1, String param2) {
        ActiveJobsContratistasFragment fragment = new ActiveJobsContratistasFragment();
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
        View view = inflater.inflate(R.layout.fragment_active_jobs_contratistas, container, false);
        recycler = view.findViewById(R.id.joblist_recycler_contratistas);
        manager = new LinearLayoutManager(ActiveJobsContratistasFragment.this.getContext());
        recycler.setLayoutManager(manager);

        final List<PermisoTrabajo> dataList = mListener.retriveJobs();


        adapter = new PermisosContratistasAdapter(dataList, new ListenerClick() {
            @Override
            public void click(int position) {
                Log.d("POSICION", String.valueOf(position));
                //mListener.showPersonalList(list.get(position));

            }
        }, new PermisosContratistasAdapter.Transition() {
            @Override
            public void close(int pos) {
                //Toast.makeText(getContext(), "ir al fragment de cierre", Toast.LENGTH_LONG).show();
                mListener.launchComment(pos);
            }
        });

        recycler.setAdapter(adapter);
        return view;
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

        List<PermisoTrabajo> retriveJobs();

        void launchComment(int pos);
    }
}
