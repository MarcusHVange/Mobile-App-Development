package dk.itu.moapd.x9.mhiv.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.mhiv.databinding.RowItemBinding
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

class CustomAdapter(private var data: List<TrafficReportModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trafficReport: TrafficReportModel) {
            binding.reportTitleCardText.text = trafficReport.reportTitle
            binding.reportTypeCardText.text = trafficReport.reportType
            binding.reportDescriptionCardText.text = trafficReport.reportDescription
            binding.reportPriorityText.text = trafficReport.reportPriority
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    fun submitList(items: List<TrafficReportModel>) {
        if (items.size <= data.size) return
        data = items
        notifyItemInserted(items.lastIndex)
    }
}
