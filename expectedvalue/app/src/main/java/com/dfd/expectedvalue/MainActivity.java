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

//         mydb.execSQL("INSERT INTO name_slot (name) SELECT ('GOD') FROM name_slot WHERE NOT EXISTS(SELECT COUNT(*) FROM name_slot WHERE name = ('GOD'))");

//        mydb.execSQL("INSERT INTO name_slot (name) SELECT ('GOD') FROM name_slot WHERE NOT EXISTS(SELECT * FROM name_slot WHERE name = ('GOD'))");

        String query_select = "SELECT * FROM name_slot";
        Cursor cursor = mydb.rawQuery(query_select, null);

        String result_str="";

        while (cursor.moveToNext()){
            int index_name = cursor.getColumnIndex("name");
            String name = cursor.getString(index_name);
            result_str +="NAME:" + name;
        }
        ((TextView)findViewById(R.id.textView)).setText(result_str);

        mydb.close();
    }

    public void onClick(View view) {

    }
}
