package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class CafeteriasFragment extends Fragment {

    private CafeteriaListViewModel mCafeteriaListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mCafeteriaListViewModel =
                new ViewModelProvider(this).get(CafeteriaListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cafeterias, container, false);

        RecyclerView recyclerViewCafeterias = root.findViewById(R.id.recyclerView_cafeterias);
        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter(root.getContext());
        recyclerViewCafeterias.setAdapter(adapterCafeterias);

        // Add an observer on the LiveData returned by getCafeterias.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the cafeterias in the adapter.
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), adapterCafeterias::setCafeteriaList);
        return root;
    }
}
