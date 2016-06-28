package com.dfd.expectedvalue;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;

/**
 * Created by a61-201405-2055 on 16/06/27.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    static SQLiteDatabase mydb;
    static String[] nameList = new String[5];
    static Integer[] numList = new Integer[5];
    static int whichName;
    static EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);

        Button resultBtn = (Button)findViewById(R.id.resultBtn);
        resultBtn.setOnClickListener(this);

        nameList[0] = "AAA";
        nameList[1] = "BBB";
        nameList[2] = "CCC";
        nameList[3] = "DDD";
        nameList[4] = "EEE";

        numList[0] = 123;
        numList[1] = 456;
        numList[2] = 789;
        numList[3] = 147;
        numList[4] = 258;

        insertDB();

    }

    public void insertDB() {
        //DBのオープン処理
        DataBase db = new DataBase(getApplicationContext());
        mydb = db.getWritableDatabase();

        for (int i = 0; i < nameList.length; ++i) {
            long counters = DatabaseUtils.queryNumEntries(mydb, "name_slot", "name = '"+nameList[i]+"'");

            if (counters == 0) {
                mydb.execSQL("INSERT INTO name_slot (name, numbers) VALUES ('"+nameList[i]+"', "+numList[i]+")");
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

        final Spinner spinner = (Spinner)findViewById(R.id.name_spinner);
        spinner.setAdapter(names);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText = (EditText)findViewById(R.id.input_number);
                editText.setText("");
                ((TextView)findViewById(R.id.resultTextView)).setText("");
                whichName = spinner.getSelectedItemPosition();
                ((TextView)findViewById(R.id.resultview)).setText(String.valueOf(numList[whichName]));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mydb.close();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.resultBtn) {
            editText = (EditText)findViewById(R.id.input_number);
            editText.selectAll();
            String inputNumbers = editText.getText().toString();
            if (inputNumbers.length() != 0){
                double resultOfMath = Math.pow((numList[whichName]), (Integer.parseInt(inputNumbers)));
                ((TextView)findViewById(R.id.resultTextView)).setText(String.valueOf(resultOfMath));
            } else {
                Toast.makeText(this, "ゲーム数を入力してください", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
