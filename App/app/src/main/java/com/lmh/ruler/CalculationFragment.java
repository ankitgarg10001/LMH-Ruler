package com.lmh.ruler;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCalculationFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalculationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalculationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private OnCalculationFragmentInteractionListener mListener;
    private EditText etMarbleRate, etTransportCharge;
    private TextView tvMarbleQuantity, tvMarblePieces;
    private TextView tvTotalPriceForCustomer;
    TextWatcher updatePriceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateValuesFromView();
            mListener.updatePrice();
            updateViewsFromValues(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
//    private TextView tvOpenList;

    public CalculationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CalculationFragment newInstance() {
        CalculationFragment fragment = new CalculationFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculation, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCalculationFragmentInteractionListener) {
            mListener = (OnCalculationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCalculationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeComponents();
        initializeListeners();
        updateViewsFromValues(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewsFromValues(true);
    }

    public void initializeComponents() {
        etMarbleRate = (EditText) getView().findViewById(R.id.etMarbleRate);
        tvMarbleQuantity = (TextView) getView().findViewById(R.id.tvMarbleQuantity);
        tvMarblePieces = (TextView) getView().findViewById(R.id.tvMarblePieces);
        etTransportCharge = (EditText) getView().findViewById(R.id.etTransportCharge);
        tvTotalPriceForCustomer = (TextView) getView().findViewById(R.id.tvTotalPriceForCustomer);
//        tvOpenList = (TextView) getView().findViewById(R.id.tvOpenList);

    }

    private void initializeListeners() {
        etMarbleRate.addTextChangedListener(updatePriceWatcher);
        etTransportCharge.addTextChangedListener(updatePriceWatcher);
        /*tvOpenList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.swapFragment();
            }
        });*/
    }

    private void detachListeners() {
        etMarbleRate.removeTextChangedListener(updatePriceWatcher);
        etTransportCharge.removeTextChangedListener(updatePriceWatcher);

    }

    public void updateValuesFromView() {
        try {
            mListener.setMarbleRate(Double.parseDouble(etMarbleRate.getText().toString()));
        } catch (Exception e) {
            mListener.setMarbleRate(0.0);
        }
        try {
            mListener.setTransportCharge(Double.parseDouble(etTransportCharge.getText().toString()));
        } catch (Exception e) {
            mListener.setTransportCharge(0.0);
        }
    }

    public void updateViewsFromValues(boolean shouldUpdateEditText) {
        detachListeners();
        if (shouldUpdateEditText) {
            if (etMarbleRate != null)
                etMarbleRate.setText(formatter.format(mListener.getMarbleRate()));
            if (etTransportCharge != null)
                etTransportCharge.setText(formatter.format(mListener.getTransportCharge()));
        }
        if (tvMarbleQuantity != null)
            tvMarbleQuantity.setText(formatter.format(mListener.getMarbleQuantity()));
        if (tvTotalPriceForCustomer != null)
            tvTotalPriceForCustomer.setText(formatter.format(mListener.getTotalPriceForCustomer()));
        if (tvMarblePieces != null)
            tvMarblePieces.setText(String.valueOf(mListener.getMarblePieces()));
        initializeListeners();
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
    public interface OnCalculationFragmentInteractionListener {
        void updatePrice();

        Double getMarbleRate();

        void setMarbleRate(Double value);

        Double getMarbleQuantity();

        Double getTransportCharge();

        void setTransportCharge(Double value);

        Double getTotalPriceForCustomer();

        Integer getMarblePieces();
    }
}
