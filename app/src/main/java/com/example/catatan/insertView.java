package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.support.v7.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class insertView extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    EditText editFilename, editContent;
    Button buttonSimpan;
    boolean isEditable = false;
    String filename = "";
    String tempCatatan = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editFilename    = findViewById(R.id.editFilename);
        editContent     = findViewById(R.id.editContent);
        buttonSimpan    = findViewById(R.id.buttonSimpan);

        buttonSimpan.setOnClickListener(this);

        Bundle extras   = getIntent().getExtras();

        if (extras != null){
            filename = extras.getString("filename");
            editFilename.setText(filename);
            getSupportActionBar().setTitle("Ubah Catatan");
        }else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }
        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23){
            if (periksaIzinPenyimpanan()){
                bacaFile();
            }else {
                bacaFile();
            }
        }
    }

    public boolean periksaIzinPenyimpanan(){
        if (Build.VERSION.SDK_INT >=23){
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return true;
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (eventID ==1){
                        bacaFile();
                    }
                    else{
                        tampilkanDialogKonfirmasi();
                    }
                }
                break;

        }
    }

    private void bacaFile(){
        String path = Environment.getExternalStorageDirectory().toString() +"/proyek1";
        File file = new File(path, editFilename.getText().toString());
        if (file.exists()){
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null){
                    text.append(line);
                    line    = br.readLine();
                }
                br.close();
            } catch (IOException e){
                System.out.println("Error " + e.getMessage());
            }
            tempCatatan = text.toString();
            editContent.setText(text.toString());
        }
    }

    private void buatDanUbah(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return;
        }
        String path = Environment.getExternalStorageDirectory().toString()+"/proyek1";
        File parent = new File(path);
        if(parent.exists()){
            File file = new File(path,editFilename.getText().toString());
            FileOutputStream outputStream=null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(editContent.getText().toString());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            parent.mkdir();
            File file = new File(path,editFilename.getText().toString());
            FileOutputStream outputStream=null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(editContent.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        onBackPressed();
    }

    private void tampilkanDialogKonfirmasi(){
        new AlertDialog.Builder(this).setTitle("Simpan Catatan").setMessage("Anda yakin?")
                .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buatDanUbah();
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    private void onClick(View v){
        switch (v.getId()){
            case    R.id.buttonSimpan: eventID = 2;
                if (!tempCatatan.equals(editContent.getText().toString())){
                    if (Build.VERSION.SDK_INT >= 23){
                        tampilkanDialogKonfirmasi();
                    }else  {
                        tampilkanDialogKonfirmasi();
                    }
                }
                break;
        }
    }
    public void onBackPressed(){
        if (!tempCatatan.equals(editContent.getText().toString())){
            tampilkanDialogKonfirmasi();
        }
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}