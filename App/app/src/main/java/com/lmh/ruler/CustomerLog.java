package com.lmh.ruler;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ankit Garg on 15-08-2016.
 */
public class CustomerLog {
    List<Piece> mPieceDataSet = new ArrayList<>();


    String name;
    Double marbleRate = 0.0, marbleQuantity = 0.0, transportCharge = 0.0, totalPriceForCustomer = 0.0;

    public static CustomerLog fromJson(String JSON) {

        Gson gson = new Gson();
        return gson.fromJson(JSON, CustomerLog.class);
    }

    public void clear() {
        name = "";
        marbleRate = 0.0;
        marbleQuantity = 0.0;
        transportCharge = 0.0;
        totalPriceForCustomer = 0.0;
        mPieceDataSet = new ArrayList<>();
    }

    public Double getMarbleRate() {
        return marbleRate;
    }

    public void setMarbleRate(Double value) {
        this.marbleRate = value;
    }

    public Double getMarbleQuantity() {
        return marbleQuantity;
    }

    public void setMarbleQuantity(Double value) {
        this.marbleQuantity = value;
    }

    public Double getTransportCharge() {
        return transportCharge;
    }

    public void setTransportCharge(Double value) {
        this.transportCharge = value;

    }

    public Double getTotalPriceForCustomer() {
        return totalPriceForCustomer;
    }

    public void setTotalPriceForCustomer(Double value) {
        this.totalPriceForCustomer = value;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Piece> getmPieceDataSet() {
        return mPieceDataSet;
    }

    public void setmPieceDataSet(List<Piece> pieceList) {
        this.mPieceDataSet = pieceList;
    }

    public String toJson() {
        if (TextUtils.isEmpty(getName())) {
            setName(getMarbleRate().toString());
        }
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Integer getPiecesCount() {
        Integer count = 0;
        for (Piece piece : mPieceDataSet) {
            if (piece != null && piece.getCount() != null)
                count += piece.getCount();
        }
        return count;
    }
}
