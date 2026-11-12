package com.example.qcdefectchecklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InspectionAdapter(
    private var items: List<Inspection>,
    private val onEditClick: (Inspection) -> Unit,
    private val onDeleteClick: (Inspection) -> Unit
) : RecyclerView.Adapter<InspectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBatchAndComponent: TextView = view.findViewById(R.id.tvBatchAndComponent)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDefectDetail: TextView = view.findViewById(R.id.tvDefectDetail)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inspection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvBatchAndComponent.text = "${item.batchCode} - ${item.componentName}"
        holder.tvStatus.text = item.status
        holder.tvStatus.setTextColor(if (item.status == "PASS") Color.parseColor("#388E3C") else Color.parseColor("#D32F2F"))
        holder.tvDefectDetail.text = "Cacat: ${item.defectType} (${item.defectCount} dari ${item.sampleSize} sampel)"

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Inspection>) {
        items = newItems
        notifyDataSetChanged()
    }
}