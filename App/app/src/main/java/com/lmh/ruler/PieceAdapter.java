package com.lmh.ruler;


import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class PieceAdapter extends RecyclerView.Adapter<PieceAdapter.Holder> {


    private static int selectedItem = -1;
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private final PieceFragment.OnPieceListFragmentInteractionListener listFragmentInteractionListener;

    public PieceAdapter(PieceFragment.OnPieceListFragmentInteractionListener listFragmentInteractionListener) {
        if (listFragmentInteractionListener == null) {
            throw new IllegalArgumentException();
        }
        this.listFragmentInteractionListener = listFragmentInteractionListener;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public PieceAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.piece, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final PieceAdapter.Holder holder, int position) {

        holder.currentPiece = listFragmentInteractionListener.getPieceList().get(position);

        holder.tvItemIndex.setText(position + 1 + "");
        if (holder.currentPiece.getLength() > 0.0)
            holder.etLength.setText(holder.currentPiece.getLength().toString());
        else
            holder.etLength.setText("");

        if (holder.currentPiece.getWidth() > 0)
            holder.etWidth.setText(holder.currentPiece.getWidth().toString());
        else
            holder.etWidth.setText("");
        holder.tvCalculated.setText(formatter.format(holder.currentPiece.calculateQuantity()));
        holder.tvCount.setText(holder.currentPiece.getCount().toString());
        /*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        //TODO correct this, toggeling currently*/
        holder.tvDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFragmentInteractionListener.changeCount(holder, false);
            }
        });
        holder.tvIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFragmentInteractionListener.changeCount(holder, true);
            }
        });
        if (selectedItem == position)
            holder.etLength.requestFocus();
    }

    @Override
    public int getItemCount() {
        return (null != listFragmentInteractionListener.getPieceList() ? listFragmentInteractionListener.getPieceList().size() : 0);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private static final String WIDTH = "width";
        private static final String LENGTH = "length";
        TextView tvItemIndex;
        EditText etLength;
        EditTextListener lengthEditTextListener;
        EditTextListener widthEditTextListener;
        EditText etWidth;
        TextView tvCalculated;
        TextView tvCount;
        ImageButton tvDecrease;
        ImageButton tvIncrease;
        Piece currentPiece;

        public Holder(View view) {
            super(view);
            etLength = (EditText) view.findViewById(R.id.etLength);
            etWidth = (EditText) view.findViewById(R.id.etWidth);
            tvCalculated = (TextView) view.findViewById(R.id.tvCalculated);
            tvCount = (TextView) view.findViewById(R.id.tvCount);
            tvDecrease = (ImageButton) view.findViewById(R.id.tvDecrease);
            tvIncrease = (ImageButton) view.findViewById(R.id.tvIncrease);
            this.tvItemIndex = (TextView) view.findViewById(R.id.tvItemIndex);
            this.widthEditTextListener = new EditTextListener(Holder.WIDTH);
            this.widthEditTextListener.updateHolder(this);
            this.lengthEditTextListener = new EditTextListener(Holder.LENGTH);
            this.lengthEditTextListener.updateHolder(this);
            this.etWidth.addTextChangedListener(widthEditTextListener);
            this.etLength.addTextChangedListener(lengthEditTextListener);
            this.etLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        handled = etWidth.requestFocus();
                    }
                    return handled;
                }
            });
            this.etWidth.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        listFragmentInteractionListener.addPiece();
                        handled = true;
                    }
                    return handled;
                }
            });
        }

    }

    private class EditTextListener implements TextWatcher {
        private final String mode;
        private Holder holder;

        public EditTextListener(String length) {
            this.mode = length;
        }

        public void updateHolder(Holder holder) {
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            int position = holder.getLayoutPosition();
            switch (mode) {
                case Holder.LENGTH:
                    try {
                        holder.currentPiece.setLength(Double.parseDouble(charSequence.toString()));
                    } catch (Exception e) {
                        holder.currentPiece.setLength(0.0);
                    }

                    break;
                case Holder.WIDTH:
                    try {
                        holder.currentPiece.setWidth(Double.parseDouble(charSequence.toString()));
                    } catch (Exception e) {
                        holder.currentPiece.setWidth(0.0);
                    }

                    break;

            }
            listFragmentInteractionListener.updatePriceAndView();
            holder.tvCalculated.setText(formatter.format(holder.currentPiece.calculateQuantity()));
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    }

}