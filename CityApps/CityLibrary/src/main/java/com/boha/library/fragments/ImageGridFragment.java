package com.boha.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.boha.cityapps.R;
import com.boha.library.adapters.PictureGridLocalAdapter;
import com.boha.library.util.DividerItemDecoration;

/**
 * Fragment to house local pictures
 */
public class ImageGridFragment extends Fragment implements PageFragment {

    private ImageGridFragmentListener mListener;

    public static ImageGridFragment newInstance() {
        ImageGridFragment fragment = new ImageGridFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public ImageGridFragment() {
        // Required empty public constructor
    }

    PictureGridLocalAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    View view;
    RecyclerView grid;
    Button btnmap;
    TextView txtTitle;
    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_picture_grid, container, false);
        txtTitle = (TextView) view.findViewById(R.id.PIC_GRID_title);
        btnmap = (Button)view.findViewById(R.id.PIC_GRID_btnMap);
        grid = (RecyclerView)view.findViewById(R.id.PIC_GRID_recyclerView);
        ctx = getActivity();

        txtTitle.setText("eThekwini");

        grid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        grid.setItemAnimator(new DefaultItemAnimator());
        grid.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.VERTICAL));

        adapter = new PictureGridLocalAdapter(R.layout.full_photo_item,ctx,
                new PictureGridLocalAdapter.PictureGridListener() {
            @Override
            public void onPictureClicked(int position) {
                mListener.onPictureClicked(position);
            }
        });

        grid.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ImageGridFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ImageGridFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ImageGridFragmentListener {
        public void onPictureClicked(int position);
    }

    static final String LOG = ImageGridFragment.class.getSimpleName();
}
