package com.firebase.notti.ui.note_groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.notti.databinding.FragmentGroupsBinding;

public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private GroupsViewModel groupsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Initialize ViewModel using ViewModelProvider
        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        // Set up View Binding
        binding = FragmentGroupsBinding.inflate(inflater, container, false);

        // Observe LiveData in ViewModel
        groupsViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            binding.textGroups.setText(text);
        });

        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGroups;
        groupsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}