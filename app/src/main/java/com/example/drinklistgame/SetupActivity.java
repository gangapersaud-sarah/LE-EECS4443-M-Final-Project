package com.example.drinklistgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SetupActivity extends Activity {
    private EditText nameEditText, studentIdEditText, emailEditText;
    private Spinner interactionSpinner;
    private CheckBox consentCheckbox;
    private Button startButton;

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
                startActivity(new Intent(SetupActivity.this, MainActivity.class));
            }
        });
    }

    private void saveDataToCsv(String name, String studentId, String email, String interactionType) {
        String fileName = "Participate_" + name + "_" + interactionType + ".csv";
        File csvFile = new File(getExternalFilesDir(null), fileName);

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Name,Student ID,Email,Interaction Type\n");
            writer.append(name).append(",");
            writer.append(studentId).append(",");
            writer.append(email).append(",");
            writer.append(interactionType).append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
