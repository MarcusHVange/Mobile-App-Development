package dk.itu.moapd.x9.mhiv.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.mhiv.databinding.RowItemBinding
import dk.itu.moapd.x9.mhiv.domain.model.DummyModel

class CustomAdapter(private val data: List<DummyModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dummy: DummyModel) {
            binding.reportTitleCardText.text = dummy.reportTitle
            binding.reportTypeCardText.text = dummy.reportType
            binding.reportDescriptionCardText.text = dummy.reportDescription
            binding.reportPriorityText.text = dummy.reportPriority
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
}