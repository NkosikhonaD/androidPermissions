package com.example.intentexampleonfiles;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity
{
    ActivityResultLauncher activityResultLauncher;
    // needed when we start an intent activity that must return
    // results to the original activity

    TextView displayFileTexview;
    Button loadFileButton;
    private static final int WRITE_PERMISSION_CODE= 1;
    private static final int READ_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayFileTexview = findViewById(R.id.textView);
        loadFileButton = findViewById(R.id.button);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent dataIntent = result.getData();
                if (dataIntent != null) {
                    Uri fileUri = dataIntent.getData();
                    String lines = " ";
                    try {
                        // Java stuff

                        InputStream inputStream = getContentResolver().openInputStream(fileUri);
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        String line = br.readLine();
                        while (line!=null)
                        {
                            lines+=line+"\n";
                            line= br.readLine();

                        }
                        displayFileTexview.setText(lines);
                        inputStream.close();
                        br.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


      loadFileButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_PERMISSION_CODE);
          }
      });
    }


    /*
    Will use implicit intent ACTION_GET_CONTENT
    This intent allows us to pick content from device storage
    Content can be file, images, videos ect..
     **/
    private void filePicker()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("*/*");
        activityResultLauncher.launch(intent);


        // browse any file */*
        // browse pdf only "application/pdf"
        // check out other content types.


    }

    /**
     * Checks if permission is not granted, if NOT granted
     * requests the permission
     * @param permission String representing the permission ie "READ_STORAGE_PERMISSION"
     * @param requestCode programmer defined final int, that uniquely identifies a PERSMISION in the application
     */
    public void checkAndRequestPermission(String permission, int requestCode)
    {
         if(ContextCompat.checkSelfPermission(MainActivity.this,permission) == PackageManager.PERMISSION_DENIED)
         {
             ActivityCompat.requestPermissions(MainActivity.this,new String[] {permission},requestCode);
         }
         else
         {
             Toast.makeText(MainActivity.this,"permission already granted",Toast.LENGTH_SHORT).show();
         }
    }

    /**
     * A callback method, Called to recieve the result of asking for permission, result can either be refused or granted
     * @param requestCode The programmer defined permission code
     * @param permissions array of permissions strings
     * @param grantResults String array that contains the results of requesting a permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_PERMISSION_CODE)
        {
            if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Write permission granted",Toast.LENGTH_SHORT).show();
               // Put logic to execute when permission granted.
                filePicker();
            }
            else
            {
                Toast.makeText(MainActivity.this,"write permission denied",Toast.LENGTH_SHORT).show();
            }

        }
        else if(requestCode==WRITE_PERMISSION_CODE)
        {
            // check if grandResults.length>1 && grandResult[0] == PackageManager.PERMISSION_GRANTED

        }
        else
        {

        }
    }
}