package com.example.drinklistgame;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SetupActivity extends Activity {
    private static final int WRITE_REQUEST_CODE = 101;
    private EditText nameEditText, studentIdEditText, emailEditText;
    private Spinner interactionSpinner;
    private CheckBox consentCheckbox;
    private Button startButton;
    private String nameOfFile;

    private String name, studentId, email, interactionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        nameEditText = findViewById(R.id.nameEditText);
        studentIdEditText = findViewById(R.id.EditStudentID);
        emailEditText = findViewById(R.id.EditEmail);
        interactionSpinner = findViewById(R.id.interactionSpinner);
        consentCheckbox = findViewById(R.id.checkbox);
        startButton = findViewById(R.id.start_button);

        consentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable or disable the start button based on the checkbox state
                startButton.setEnabled(isChecked);
            }
        });

        setupSpinner();
        setupStartButton();
    }

    private void setupSpinner() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Tap & Tap");
        options.add("Tap & Drag");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        interactionSpinner.setAdapter(adapter);
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String studentId = studentIdEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String interactionType = interactionSpinner.getSelectedItem().toString();
                //saveDataToExcel(name, studentId, email, interactionType);
                saveDataToCsv(name, studentId, email, interactionType);
                //startActivity(new Intent(SetupActivity.this, MainActivity.class));
                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                intent.putExtra("interactionType", interactionType);
                startActivity(intent);

            }
        });
    }

    private void saveDataToCsv(String name, String studentId, String email, String interactionType) {
        nameOfFile = "Participate_" + name + "_" + interactionType + ".csv";
        File dir = new File(getExternalFilesDir(null), "");
        Log.i("my paths", String.valueOf(dir.exists()));
        if(!dir.exists()){
            dir.mkdir();
        }
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.interactionType= interactionType;
        //
        File csvFile = new File(dir, nameOfFile);
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Name,Student ID,Email,Interaction Type\n");
            writer.append(name).append(",");
            writer.append(studentId).append(",");
            writer.append(email).append(",");
            writer.append(interactionType).append("\n");
            writer.flush();
            Log.i("my second paths", String.valueOf(getExternalFilesDir(null)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendFile(Uri.fromFile(csvFile));
    }
    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    private void sendFile(Uri pickerInitialUri) {
        // when you create document, you need to add Intent.ACTION_CREATE_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested Mime type
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, nameOfFile);

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null
                            && data.getData() != null) {
                        writeInFile(data.getData());
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }
    private void writeInFile(Uri uri) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            //bw.write(text);
            bw.append("Name,Student ID,Email,Interaction Type\n");
            bw.append(name).append(",");
            bw.append(studentId).append(",");
            bw.append(email).append(",");
            bw.append(interactionType).append("\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(SetupActivity.this, MainActivity.class);
        //not working
//        Bundle b = new Bundle();
//        b.putString("csv_uri", String.valueOf(uri));
//        i.putExtras(b);
        startActivity(i);


    }
    private OutputStream writeInFile2(Uri uri, String text) {
        OutputStream outputStream;
        try {
            return outputStream = getContentResolver().openOutputStream(uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    /*private void saveDataToExcel(String name, String studentId, String email, String interactionType) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Participate" + name + interactionType);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Name");
        row.createCell(1).setCellValue("Student ID");
        row.createCell(2).setCellValue("Email");
        row.createCell(3).setCellValue("Interaction Type");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(name);
        dataRow.createCell(1).setCellValue(studentId);
        dataRow.createCell(2).setCellValue(email);
        dataRow.createCell(3).setCellValue(interactionType);

        String fileName = "Participate" + name + interactionType + ".xlsx";
        try (FileOutputStream fileOut = openFileOutput(fileName, MODE_PRIVATE)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
