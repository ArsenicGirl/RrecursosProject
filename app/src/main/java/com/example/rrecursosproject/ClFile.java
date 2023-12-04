package com.example.rrecursosproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;

public class ClFile {
    private static final int REQUEST_CODE = 23;
    private Context context;
    private Activity activity;

    public ClFile(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private boolean statusPermissionES(){
        int response = ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(response == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }

    private void requestPermissionES(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

            //estion de respuesta (faltaba)
            Toast.makeText(context, "Permiso otorgado", Toast.LENGTH_SHORT).show();
        }
    }

    //File que es una clase de java para poder trabajar con archivos
    private void createDir(File file){
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /*
    public void saveFile(){

    }*/

    public void saveFile(String nameFile, String info) {

        File directory = null;
        requestPermissionES();
        if (statusPermissionES()){
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                    directory = new File(Environment.getExternalStorageDirectory(), "MY APLICACTION");
                    createDir(directory);
                    Toast.makeText(context, "Ruta "+ directory, Toast.LENGTH_LONG).show();
                }else{
                    directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), "ArchivoAPLI");
                    createDir(directory);
                    Toast.makeText(context, "Ruta "+ directory, Toast.LENGTH_LONG).show();
                }
                if (directory != null){

                    //PARA CREAR EL ARCHIVO

                    File file = new File(directory, nameFile);
                    FileWriter writer = new FileWriter(file);
                    writer.append(info);
                    writer.flush();
                    writer.close();
                    Toast.makeText(context, "Archivo guardado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "No se pudo guardar el archivo", Toast.LENGTH_LONG).show();
                }
            }catch (Exception exc){
                exc.printStackTrace();
                Toast.makeText(context, ""+exc, Toast.LENGTH_LONG).show();
            }
        }
    }
}


