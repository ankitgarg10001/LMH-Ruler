package com.lmh.ruler;

/**
 * Created by Ankit Garg on 25-06-2016.
 */
public class Piece {
    Double length = 0.0;
    Double width = 0.0;
    Integer count = 1;
    Double sum = 0.0;

    public Piece() {

    }

    public Piece(Double length, Double width) {
        this.length = length;
        this.width = width;
    }

    public void setEmpty() {
        length = 0.0;
        width = 0.0;
        count = 1;
        sum = 0.0;
    }

    public Double calculateQuantity() {
        try {
            setSum(getLength() * getWidth() * getCount() / 144);
            return getSum();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
