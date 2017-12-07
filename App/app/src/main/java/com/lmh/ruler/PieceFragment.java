package com.lmh.ruler;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPieceListFragmentInteractionListener}
 * interface.
 */
public class PieceFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnPieceListFragmentInteractionListener mListener;
    private PieceAdapter mPieceAdapter;
    private RecyclerView mPieceRecyclerView;
    private List<Piece> mPieceList;
    private RemovedPiece currentRemoved;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PieceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PieceFragment newInstance(int columnCount) {
        PieceFragment fragment = new PieceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        } else {
            mColumnCount = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_piece_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mPieceRecyclerView = (RecyclerView) view;
            mPieceList = mListener.getPieceList();
            mPieceRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
            mPieceRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mPieceAdapter = new PieceAdapter(mListener);
            mPieceRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mPieceRecyclerView.setAdapter(mPieceAdapter);
            addSwipeGestureForRecyclerViewItems();
            if (mPieceList.size() < 1)
                addPiece();
        }
        return view;
    }

    public void addSwipeGestureForRecyclerViewItems() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                mPieceList.get(viewHolder.getAdapterPosition()).setEmpty();
                final Integer removedPos = viewHolder.getAdapterPosition();
                currentRemoved = new RemovedPiece(removedPos, mPieceList.remove(viewHolder.getAdapterPosition()));
                mPieceAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                mListener.updatePriceAndView();
                checkUndoPossible();
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mPieceRecyclerView);
    }

    private void checkUndoPossible() {
        if (currentRemoved == null) {
            return;
        }
        Snackbar.make(getView(), "Removed " + currentRemoved.getPiece().getLength() + "*" + currentRemoved.getPiece().getWidth() + "*" + currentRemoved.getPiece().getCount(), Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPieceList.add(currentRemoved.getPos(), currentRemoved.getPiece());
                mPieceAdapter.notifyItemInserted(currentRemoved.getPos());
                mListener.updatePriceAndView();
            }
        }).show();
    }


    void addPiece() {

        //TODO make ficus on new component on addition new row
        mPieceList.add(new Piece());
        mPieceAdapter.setSelectedItem(mPieceList.size() - 1);
        mPieceAdapter.notifyItemChanged(mPieceList.size() - 1);
        //mListener.updatePrice();
        mPieceRecyclerView.smoothScrollToPosition(mPieceList.size() - 1);
        PieceAdapter.Holder viewHolderForLayoutPosition = (PieceAdapter.Holder) mPieceRecyclerView.findViewHolderForLayoutPosition(mPieceList.size() - 2);
        if (viewHolderForLayoutPosition != null)
            viewHolderForLayoutPosition.etLength.requestFocus();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPieceListFragmentInteractionListener) {
            mListener = (OnPieceListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPieceListFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.setPieceList(mPieceList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateList() {
        this.mPieceList = mListener.getPieceList();
        mPieceAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPieceListFragmentInteractionListener {
        List<Piece> getPieceList();

        void setPieceList(List<Piece> pieceList);

        void addPiece();

        void updatePriceAndView();

        void changeCount(PieceAdapter.Holder holder, Boolean b);
    }
}
