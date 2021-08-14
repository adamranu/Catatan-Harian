package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE=100;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Aplikasi Catatan");
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, insertView.class);
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                intent.putExtra("filename", data.get("nama").toString());
                Toast.makeText(MainActivity.this, "Coba klik " +data.get("nama"), Toast.LENGTH_LONG).show();

                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                Tampilkan();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=23){
            if(periksaIzinPenyimpanan()){
                ambilListFile();
            }
        }else {
            ambilListFile();
        }
    }

    public boolean periksaIzinPenyimpanan(){
        if (Build.VERSION.SDK_INT>=23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE }
                , REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ambilListFile();
                }
                break;

        }
    }

    public void ambilListFile(){
        String path = Environment.getExternalStorageDirectory().toString()+"/proyek1";
        File directory = new File(path);
        if (directory.exists()){
            File[] files = directory.listFiles();
            String[] filename = new String[files.length];
            String[] dateCreat = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM YYYY HH:mm:ss");
            ArrayList<Map<String,String>> itemDataList  = new ArrayList<>();
            for (int i=0; i<files.length; i++){
                filename[i]=files[i].getName();
                Date lastModDate=new Date(files[i].lastModified());
                dateCreat[i]=simpleDateFormat.format(lastModDate);
                Map<String,String> itemMap=new HashMap<>();
                itemMap.put("name", filename[i]);
                itemMap.put("date", dateCreat[i]);
                itemDataList.add(itemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_2,
                    new String[]{"name","date"}, new int[]{android.R.id.text1});
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            
        }
    }

    public boolean onCreateOptionmenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.action_tambah:
            Intent intent = new Intent(this,insertView.class);
            startActivity(intent);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tampilkanDialogKonfirmasihHapus(final String filename){
        new AlertDialog.Builder(this).setTitle("Hapus yang ini").setMessage("apakah anda yakin? "+filename+"?")
                .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hapusFile(filename);
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    private void hapusFile(String filename){
        String path = Environment.getExternalStorageDirectory().toString() +"proyek1";
        File file = new File(path, filename);
        if (file.exists()){
            file.delete();
        }ambilListFile();
    }
}