package com.example.qcdefectchecklist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: InspectionAdapter

    private lateinit var etBatchCode: EditText
    private lateinit var etComponentName: EditText
    private lateinit var etDefectType: EditText
    private lateinit var etSampleSize: EditText
    private lateinit var etDefectCount: EditText
    private lateinit var btnSave: Button
    private lateinit var rvInspections: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        etBatchCode = findViewById(R.id.etBatchCode)
        etComponentName = findViewById(R.id.etComponentName)
        etDefectType = findViewById(R.id.etDefectType)
        etSampleSize = findViewById(R.id.etSampleSize)
        etDefectCount = findViewById(R.id.etDefectCount)
        btnSave = findViewById(R.id.btnSave)
        rvInspections = findViewById(R.id.rvInspections)

        setupRecyclerView()
        loadData()

        btnSave.setOnClickListener {
            saveInspection()
        }
    }

    private fun setupRecyclerView() {
        adapter = InspectionAdapter(
            items = emptyList(),
            onEditClick = { item -> showEditDialog(item) },
            onDeleteClick = { item -> confirmDelete(item) }
        )
        rvInspections.layoutManager = LinearLayoutManager(this)
        rvInspections.adapter = adapter
    }

    private fun loadData() {
        val inspections = dbHelper.getAllInspections()
        adapter.updateData(inspections)
    }

    private fun saveInspection() {
        val batch = etBatchCode.text.toString().trim()
        val component = etComponentName.text.toString().trim()
        val defect = etDefectType.text.toString().trim()
        val sampleStr = etSampleSize.text.toString().trim()
        val defectCountStr = etDefectCount.text.toString().trim()

        if (batch.isEmpty() || component.isEmpty() || defect.isEmpty() || sampleStr.isEmpty() || defectCountStr.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val sampleSize = sampleStr.toInt()
        val defectCount = defectCountStr.toInt()

        if (defectCount > sampleSize) {
            Toast.makeText(this, "Jumlah cacat tidak boleh melebihi ukuran sampel", Toast.LENGTH_SHORT).show()
            return
        }

        val status = if (defectCount > 0) "REJECT" else "PASS"

        val inspection = Inspection(
            batchCode = batch,
            componentName = component,
            defectType = defect,
            sampleSize = sampleSize,
            defectCount = defectCount,
            status = status
        )

        val result = dbHelper.insertInspection(inspection)
        if (result != -1L) {
            Toast.makeText(this, "Data inspeksi berhasil disimpan", Toast.LENGTH_SHORT).show()
            clearFields()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan ke basis data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog(item: Inspection) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Inspeksi: ${item.batchCode}")

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val inputDefectType = EditText(this).apply {
            hint = "Jenis Cacat"
            setText(item.defectType)
        }
        val inputDefectCount = EditText(this).apply {
            hint = "Jumlah Cacat"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(item.defectCount.toString())
        }

        layout.addView(inputDefectType)
        layout.addView(inputDefectCount)
        builder.setView(layout)

        builder.setPositiveButton("Perbarui") { dialog, _ ->
            val updatedDefect = inputDefectType.text.toString().trim()
            val updatedDefectCount = inputDefectCount.text.toString().toIntOrNull() ?: item.defectCount

            val updatedStatus = if (updatedDefectCount > 0) "REJECT" else "PASS"

            val updatedItem = item.copy(
                defectType = updatedDefect,
                defectCount = updatedDefectCount,
                status = updatedStatus
            )

            dbHelper.updateInspection(updatedItem)
            loadData()
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun confirmDelete(item: Inspection) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Hapus rekaman inspeksi batch ${item.batchCode}?")
            .setPositiveButton("Hapus") { _, _ ->
                dbHelper.deleteInspection(item.id)
                loadData()
                Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun clearFields() {
        etBatchCode.text.clear()
        etComponentName.text.clear()
        etDefectType.text.clear()
        etSampleSize.text.clear()
        etDefectCount.text.clear()
    }
}