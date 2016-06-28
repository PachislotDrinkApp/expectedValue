package com.dfd.expectedvalue;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by a61-201405-2055 on 16/06/27.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private View focusView;
    static SQLiteDatabase mydb;
    static String[] nameList;
    static Integer[] hatsuList;
    static Integer[] heikinList;
    static Integer[] tenjouList;
    static Integer[] tenjouonkeiList;
    static Integer[] games1kList;
    static int whichName;
    static EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);

        focusView = findViewById(R.id.linearLayout);
        focusView.requestFocus();

        Button resultBtn = (Button) findViewById(R.id.resultBtn);
        resultBtn.setOnClickListener(this);

        checknumbers(getApplicationContext());

        parse(getApplicationContext());

        insertDB();

        editText = (EditText) findViewById(R.id.input_number);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    public void insertDB() {
        //DBのオープン処理
        DataBase db = new DataBase(getApplicationContext());
        mydb = db.getWritableDatabase();

        for (int i = 0; i < nameList.length; ++i) {
            long counters = DatabaseUtils.queryNumEntries(mydb, "name_slot", "name = '" + nameList[i] + "'");
            if (nameList[i] != null) {
                if (counters == 0) {
                    mydb.execSQL("INSERT INTO name_slot (name, hatsu, heikin, tenjou, tenjouonkei, gamek) VALUES ('" + nameList[i] + "', " + hatsuList[i] + ", " + heikinList[i] + ", " + tenjouList[i] + ", " + tenjouonkeiList[i] + ", " + games1kList[i] + ")");
                }
            }
        }

        String query_select = "SELECT * FROM name_slot";
        Cursor cursor = mydb.rawQuery(query_select, null);
        cursor.moveToFirst();

        ArrayAdapter names = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        for (int i = 0; i < cursor.getCount(); i++) {
            names.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }

        final Spinner spinner = (Spinner) findViewById(R.id.name_spinner);
        spinner.setAdapter(names);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText = (EditText) findViewById(R.id.input_number);
                editText.setText("");
                ((TextView) findViewById(R.id.resultTextView)).setText("");
                whichName = spinner.getSelectedItemPosition();
                ((TextView) findViewById(R.id.resultview)).setText("天井ゲーム数 : " + String.valueOf(tenjouList[whichName]) + "ゲーム");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mydb.close();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.resultBtn) {
            editText = (EditText) findViewById(R.id.input_number);
            editText.selectAll();
            String inputNumbers = editText.getText().toString();
            if (inputNumbers.length() != 0) {

                //汚い
                double leftgames = tenjouList[whichName] - (Integer.parseInt(inputNumbers));
                double onegame = (hatsuList[whichName]-1);
                double nowgame = onegame/(hatsuList[whichName]);
                double reachprob = (Math.pow(nowgame, leftgames))*100;
                double onekprob = (1 - Math.pow(nowgame, games1kList[whichName]))*100;
                double howmuch = (100 - reachprob) / onekprob;
                double howmanymedals = howmuch * 50;
                double tenkaku = tenjouonkeiList[whichName] - howmanymedals;
                double tenget = tenkaku * reachprob / 100;

                int def = 50;
                double sum = 0;
                double sumnow = heikinList[whichName];
                for (int i = 0; i <= howmuch; i++ ) {
                    sumnow = sumnow - def;
                    sum = sum + sumnow;
                }

                double befget = sum * onekprob / 100;

                double resultOfMath = tenget + befget;
                ((TextView) findViewById(R.id.resultTextView)).setText("期待値　: " + String.valueOf(resultOfMath) + "枚");
            } else {
                Toast.makeText(this, "ゲーム数を入力してください", Toast.LENGTH_SHORT).show();
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void checknumbers(Context context) {
        AssetManager assetManager = context.getResources().getAssets();

        try {
            //CSVファイル読み込み
            InputStream inputStream = assetManager.open("names.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            int i = 0;
            while (bufferedReader.readLine() != null) {
                i++;
            }
            nameList = new String[i];
            hatsuList = new Integer[i];
            heikinList = new Integer[i];
            tenjouList = new Integer[i];
            tenjouonkeiList = new Integer[i];
            games1kList = new Integer[i];
            bufferedReader.close();
        } catch (IOException e) {

        }
    }

    public static void parse(Context context) {
        AssetManager assetManager = context.getResources().getAssets();

        try {
            //CSVファイル読み込み
            InputStream inputStream = assetManager.open("names.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                nameList[i] = stringTokenizer.nextToken();
                hatsuList[i] = Integer.parseInt(stringTokenizer.nextToken());
                heikinList[i] = Integer.parseInt(stringTokenizer.nextToken());
                tenjouList[i] = Integer.parseInt(stringTokenizer.nextToken());
                tenjouonkeiList[i] = Integer.parseInt(stringTokenizer.nextToken());
                games1kList[i] = Integer.parseInt(stringTokenizer.nextToken());

                i++;
            }
            bufferedReader.close();
        } catch (IOException e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        focusView.requestFocus();
        return super.onTouchEvent(event);
    }
}
