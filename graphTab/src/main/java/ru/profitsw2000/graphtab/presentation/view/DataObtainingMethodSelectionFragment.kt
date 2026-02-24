package ru.profitsw2000.graphtab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentDataObtainingMethodSelectionBinding

class DataObtainingMethodSelectionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDataObtainingMethodSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDataObtainingMethodSelectionBinding.bind(
            inflater.inflate(R.layout.fragment_data_obtaining_method_selection, container, false)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}