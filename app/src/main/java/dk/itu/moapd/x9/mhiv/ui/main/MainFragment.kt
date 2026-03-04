package dk.itu.moapd.x9.mhiv.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.databinding.FragmentMainBinding
import dk.itu.moapd.x9.mhiv.domain.model.DummyModel
import dk.itu.moapd.x9.mhiv.ui.list.CustomAdapter
import dk.itu.moapd.x9.mhiv.ui.utils.viewBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CustomAdapter(createDummyData())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        parentFragmentManager.setFragmentResultListener("Report", viewLifecycleOwner) { _, bundle ->
            val reportData = bundle.getString("reportData")
            if(reportData != null) {
                Log.i("Report info", reportData)
                Toast.makeText(requireContext(), "Report created", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goToReportBtn.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_traffic_report)
        }
    }

    private fun createDummyData(): List<DummyModel> {
        val dummy1 = DummyModel(
            reportTitle = "Big highway accident",
            reportType = "accident",
            reportDescription = "Two cars have collided on the highway",
            reportPriority = "Major"
        )

        val dummy2 = DummyModel (
            reportTitle = "Police setup on roads",
            reportType = "speed camera",
            reportDescription = "Police have setup speed cameras along the roads",
            reportPriority = "Moderate"
        )

        val dummy3 = DummyModel (
            reportTitle = "Traffic jam",
            reportType = "heavy traffic",
            reportDescription = "There is a pretty heavy traffic jam on the highway",
            reportPriority = "Minor"
        )

        return listOf(dummy1, dummy2, dummy3)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}