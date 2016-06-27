package com.dfd.expectedvalue;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by a61-201405-2055 on 16/06/27.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    static SQLiteDatabase mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);

        //DBのオープン処理
        DataBase db = new DataBase(getApplicationContext());
        mydb = db.getWritableDatabase();

        long counters = DatabaseUtils.queryNumEntries(mydb, "name_slot", "name = 'GOD'");

        String aa = "GOD";
        if (counters == 0) {
            mydb.execSQL("INSERT INTO name_slot (name) SELECT '"+aa+"'");
        }

        String query_select = "SELECT name FROM name_slot";
        Cursor cursor = mydb.rawQuery(query_select, null);
        cursor.moveToFirst();

        ArrayAdapter names = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        for (int i = 0; i < cursor.getCount(); i++) {
            names.add(cursor.getColumnNames());
            cursor.moveToNext();
        }

        Spinner spinner = (Spinner)findViewById(R.id.name_spinner);
        spinner.setAdapter(names);

        mydb.close();
    }

    public void onClick(View view) {

    }
}
