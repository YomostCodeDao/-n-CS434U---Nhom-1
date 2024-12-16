package com.example.hocsqlite_1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnopen;
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    String DATABASE_NAME="qlsinhvien.db";
//Khai báo các biến giao diện
    EditText edtmalop,edttenlop,edtmasinhvien;
    Button btninsert,btnquery,btndelete,btnupdate;
    //Khai báo ListView
ListView lv;
    ArrayList<String> myList;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnopen = findViewById(R.id.btnopen);
        btnopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent=new Intent(MainActivity.this,MainActivity.class);
                startActivity(myintent);

            }
        });
        edtmalop = findViewById(R.id.edtmalop);
        edttenlop = findViewById(R.id.edttenlop);
        edtmasinhvien = findViewById(R.id.edtmasinhvien);
        btninsert = findViewById(R.id.btninsert);
        btndelete = findViewById(R.id.btndelete);
        btnupdate = findViewById(R.id.btnupdate);
        btnquery = findViewById(R.id.btnquery);
        //Tạo ListView
        lv = findViewById(R.id.lv);
        myList = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,myList);
        lv.setAdapter(myadapter);
        //Tạo và mở CSDL Sqlite
        mydatabase = openOrCreateDatabase("qlsinhvien.db",MODE_PRIVATE, null);
        //Tạo Table để chứa dữ liệu
        try {
            String sql = "CREATE TABLE Tablesinhvien(malop TEXT , tenlop Text, masv TEXT primary key)";
            mydatabase.execSQL(sql);
        }
        catch (Exception e)
        {
            Log.e("Error","Table đã tồn tại");
        }
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                String tenlop = edttenlop.getText().toString();
                String masv = edtmasinhvien.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("malop",malop);
                myvalue.put("tenlop",tenlop);
                myvalue.put("masv",masv);
                String msg ="";
                if(mydatabase.insert("Tablesinhvien",null,myvalue)==-1)
                {
                    msg ="Fail to Insert Record! ";
                }
                else {
                    msg = "Insert record Sucessfully";
                }
                Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT).show();
            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String masv = edtmasinhvien.getText().toString();
                int n = mydatabase.delete("Tablesinhvien","masv = ?",new String[]{masv});
                String msg="";
                if(n==0){
                    msg="No record to Delete";
                }
                else {
                    msg = n+"record is deleted";
                }
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String masv = edtmasinhvien.getText().toString();
                String tenlop = edttenlop.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("tenlop",tenlop);
                int n =mydatabase.update("Tablesinhvien",myvalue,"masv=?", new String[]{masv});
                String msg="";
                if(n==0){
                    msg="No record to Update";
                }
                else{
                    msg =n+"record is update";
                }
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
        btnquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.clear();
                Cursor c= mydatabase.query("Tablesinhvien",null,null,null,null,null,null);
                c.moveToNext();
                String data="";
                while(c.isAfterLast()==false){
                    data=c.getString(0)+" - "+c.getString(1)+" - "+c.getString(2);
                    c.moveToNext();
                    myList.add(data);
                }
                c.close();
                myadapter.notifyDataSetChanged();
            }
        });
    }
    private void processCopy() {
//private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try{CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
    public void CopyDataBaseFromAsset() {
// TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
// Path to the just created empty db
            String outFileName = getDatabasePath();
// if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
// Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
// transfer bytes from the inputfile to the outputfile
// Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
// Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}