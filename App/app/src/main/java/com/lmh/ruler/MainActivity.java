package com.lmh.ruler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PieceFragment.OnPieceListFragmentInteractionListener, CalculationFragment.OnCalculationFragmentInteractionListener {

    public static final String CLIPBOARD_STORAGE_NAME = "LMH_EXPORT";
    private static final int READ_REQUEST_CODE = 42;
    final String timeFormat = "HH:mm:ss a";
    final SimpleDateFormat sdfForTime = new SimpleDateFormat(timeFormat);
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    CustomerLog customerLog = new CustomerLog();
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private PieceFragment pieceFragment;
    private CalculationFragment calculationFragment;
    private String toolbarText = "";
    private boolean isPieceView = true;

    public CustomerLog getCustomerLog() {
        return customerLog;
    }

    public void setCustomerLog(CustomerLog customerLog) {
        this.customerLog = customerLog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
        pieceFragment = PieceFragment.newInstance(1);
        calculationFragment = CalculationFragment.newInstance();
        setupFragments();
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieceFragment.addPiece();
            }
        });
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.listFragment, pieceFragment);
        ft.add(R.id.mainFragment, calculationFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                aboutApp();
                return true;
            case R.id.exportData:
                exportAppData();
                copyToClipboard();
                return true;
            case R.id.importData:
//                importAppData();
                retrieveFromClipboard();
                return true;
            case R.id.clear:
                createNewClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_STORAGE_NAME, customerLog.toJson()));
    }

    private void retrieveFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip() /*&& CLIPBOARD_STORAGE_NAME.equals(clipboard.getPrimaryClipDescription().getLabel())*/) {
            ClipData.Item currentItem = clipboard.getPrimaryClip().getItemAt(0);
            if (!TextUtils.isEmpty(currentItem.getText())) {
                try {
                    loadDataFromString(currentItem.getText().toString(), false);
                    return;
                } catch (JsonSyntaxException e) {
                    showInvalidDataMessage();
                }
            }
            showInvalidDataMessage();
        }
    }

    private void showInvalidDataMessage() {
        Toast.makeText(getApplicationContext(),
                "Invalid Input Data, Please paste a valid Data and try again",
                Toast.LENGTH_LONG).show();
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("FILE", "Uri: " + uri.toString());
                loadDataFromFile(uri);
            }
        }
    }

    private void loadDataFromFile(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            reader.close();
            loadDataFromString(stringBuilder.toString(), false);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Invalid Exception, Please contact the developer",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void importAppData() {
        if (isExternalStorageReadable()) {
            performFileSearch();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please give read permissions to application to load existing data",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) && Environment.getExternalStorageDirectory().canWrite();

    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) && Environment.getExternalStorageDirectory().canRead();

    }

    private void exportAppData() {
        String dataToExport = customerLog.toJson();
        Calendar calendar = Calendar.getInstance();
        if (isExternalStorageWritable()) {
            final String path =
                    Environment.getExternalStorageDirectory() + File.separator + "LMH" +
                            File.separator + calendar.get(Calendar.YEAR) + File.separator + calendar.get(Calendar.MONTH) + File.separator;
            File folder = new File(path);
            boolean result = folder.mkdirs();
            Log.d("MyActivity", "mkdirs: " + result);
            if (folder.exists()) {
                final File file = new File(path, calendar.get(Calendar.DAY_OF_MONTH) + " - " + sdfForTime.format(calendar.getTime()) + ".backup");
                Log.d("Saving Data", file.toString());
                try {
                    file.createNewFile();
//                BufferedReader reader = new BufferedReader(new FileReader(file));
//                Log.d("reading", reader.readLine());
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(dataToExport);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                    Toast.makeText(getApplicationContext(),
                            "File successfully saved to: " + file.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please give write permissions to application to save data",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void createNewClient() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.text_input_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) promptsView
                .findViewById(R.id.textView1);
        textView.setText("Do you want to clear data for this customer?");
        userInput.setVisibility(View.GONE);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                setDataForNewCustomer();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void aboutApp() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.info_about, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setDataForNewCustomer() {
        getCustomerLog().clear();
        saveToSharedPrefs();
        loadDataFromCustomerLog();
        addPiece();
    }
   /* @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isCalculationFragmentSelected) {
            Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
        } else {
            swapFragment();
        }
    }

    public void swapFragment() {
        if (isCalculationFragmentSelected) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, pieceFragment).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, calculationFragment).addToBackStack(null).commit();
        }
        isCalculationFragmentSelected = !isCalculationFragmentSelected;
    }*/

    private void initializeComponents() {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!isPieceView) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View promptsView = li.inflate(R.layout.text_input_popup, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);
                    final TextView textView = (TextView) promptsView
                            .findViewById(R.id.textView1);
                    textView.setText("Type the header : ");
                    userInput.setVisibility(View.VISIBLE);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            customerLog.setName(userInput.getText().toString());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });
        }
    }


    @Override
    public List<Piece> getPieceList() {
        return getCustomerLog().getmPieceDataSet();
    }


    @Override
    public void setPieceList(List<Piece> pieceList) {
        getCustomerLog().setmPieceDataSet(pieceList);
        updatePriceAndView();
    }


    @Override
    public void addPiece() {
        pieceFragment.addPiece();
        updatePriceAndView();
    }

    @Override
    public void changeCount(PieceAdapter.Holder holder, Boolean isIncrease) {

        int position = holder.getLayoutPosition();
        if (isIncrease) {
            holder.currentPiece.setCount(getCustomerLog().getmPieceDataSet().get(position).getCount() + 1);
        } else {
            if (getCustomerLog().getmPieceDataSet().get(position).getCount() > 1)
                holder.currentPiece.setCount(getCustomerLog().getmPieceDataSet().get(position).getCount() - 1);
            else
                Toast.makeText(this, "Cannot decrease count any further, already at 1. You can remove item by swiping right.", Toast.LENGTH_SHORT).show();
        }

        holder.tvCount.setText(holder.currentPiece.getCount().toString());
        holder.tvCalculated.setText(formatter.format(holder.currentPiece.calculateQuantity()));

        updatePriceAndView();
    }

    public void updatePriceAndView() {
        updatePrice();
        calculationFragment.updateViewsFromValues(true);

    }

    public void updatePrice() {
        getCustomerLog().setMarbleQuantity(0.0);
        for (Piece piece : getCustomerLog().getmPieceDataSet()) {
            getCustomerLog().setMarbleQuantity(getCustomerLog().getMarbleQuantity() + piece.calculateQuantity());
        }
        if (getCustomerLog().getMarbleQuantity() != 0.0 || getCustomerLog().getMarbleRate() != 0.0)
            getCustomerLog().setTotalPriceForCustomer((getCustomerLog().getMarbleQuantity() * getCustomerLog().getMarbleRate()) + getCustomerLog().getTransportCharge());
        else
            getCustomerLog().setTotalPriceForCustomer(getCustomerLog().getTransportCharge());
    }

    @Override
    public Double getMarbleRate() {
        return getCustomerLog().getMarbleRate();
    }

    @Override
    public void setMarbleRate(Double value) {
        getCustomerLog().setMarbleRate(value);
        updateToolbarText();
    }

    @Override
    public Double getMarbleQuantity() {
        return getCustomerLog().getMarbleQuantity();
    }

    @Override
    public Double getTransportCharge() {
        return getCustomerLog().getTransportCharge();
    }

    @Override
    public void setTransportCharge(Double value) {
        getCustomerLog().setTransportCharge(value);

    }
/*    @Override
    public void setTotalPriceForCustomer(Double value) {
        getCustomerLog().setTotalPriceForCustomer(value);
    }
    @Override
    public void setMarbleQuantity(Double value) {
        getCustomerLog().setMarbleQuantity(value);
    }*/

    @Override
    public Double getTotalPriceForCustomer() {
        return getCustomerLog().getTotalPriceForCustomer();
    }

    @Override
    public Integer getMarblePieces() {
        return getCustomerLog().getPiecesCount();
    }


    public void updateToolbarText() {

        customerLog.setName(customerLog.getMarbleRate() != null ? customerLog.getMarbleRate().toString() : "");
        if (toolbar != null)
            toolbar.setTitle(customerLog.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveFromSharedPreferences();
    }

    private void loadDataFromCustomerLog() {
        pieceFragment.updateList();
        calculationFragment.updateViewsFromValues(true);
    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.customer_string_saved), customerLog.toJson());
        editor.commit();
        Toast.makeText(MainActivity.this, "Saved Current Data For retrieval", Toast.LENGTH_SHORT).show();

    }

    public void retrieveFromSharedPreferences() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String customerString = sharedPref.getString(getString(R.string.customer_string_saved), "");
        loadDataFromString(customerString, true);
    }

    private void loadDataFromString(String customerData, boolean newOnFailure) {
        boolean success = false;
        if (!TextUtils.isEmpty(customerData)) {
            try {
                this.customerLog = CustomerLog.fromJson(customerData);
                Toast.makeText(MainActivity.this, "Retrieved saved Information", Toast.LENGTH_SHORT).show();
                success = true;
            } catch (Exception e) {
                if (newOnFailure) {
                    this.customerLog = new CustomerLog();
                    setDataForNewCustomer();
                    success = true;
                }
            }
        }
        if (success) {
            loadDataFromCustomerLog();
            updateToolbarText();
        } else {
            Toast.makeText(MainActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
        }
    }
}

