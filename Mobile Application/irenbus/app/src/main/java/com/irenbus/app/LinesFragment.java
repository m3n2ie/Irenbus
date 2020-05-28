package com.irenbus.app;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.irenbus.app.model.BusInfo;
import com.irenbus.app.model.BusLine;
import com.irenbus.app.tools.CodeGenerator;
import com.irenbus.app.tools.LinesListAdapter;
import com.irenbus.app.tools.NearbyListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LinesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;

    private List<BusLine> busLines;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lines, container, false);

        recyclerView = view.findViewById(R.id.LinesRecyclerView);
        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //animalsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //animalsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeData();
        /*
        DatabaseReference refBusLines = FirebaseDatabase.getInstance().getReference("Bus-Lines");
        CodeGenerator codeGenerator = new CodeGenerator();
        String [] busLines = {"Botanic Gardens", "Chesterville", "Durban North-Umhlanga Rocks", "Fynnland-Brighton Beach",
                "Gijima-Lamontville", "Gwala's House", "Kensington-St Mathias", "Kwa Mashu J via E-F-G",
                "Kwa Mashu K via D", "Marine Garage-Merewent", "Morningside", "Mt Vernon-Malvern",
                "Musgrave Road-Mitchell Park", "Newlands-New Dawn Park", "Ntuzuma G", "Ntuzuma North Coast Rd",
                "Ntuzuma-Central Areas", "Ntuzuma-Southern Areas", "Ntuzuma-Western Areas", "People Mover Lines",
                "Pinetown-Mariannridge via Westville", "Shallcross-Hillview", "Tollgate", "Umbilo",
                "Umlazi BB Route 4", "Umlazi C J Route 3-7", "Umlazi F G Route 1", "Umlazi U Route 5",
                "Umlazi Z AA Route 6", "Umlazi-M N R Route 2", "Woodlands-Woodhaven"};

        for(String busLine : busLines){
            String code = codeGenerator.getCode();
            DatabaseReference ref = refBusLines.child(code);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", code);
            hashMap.put("bus-line", busLine);
            ref.setValue(hashMap);
        }*/

        return view;
    }

    private void initializeData(){
        busLines = new ArrayList<>();

        final DatabaseReference busLinesRef = FirebaseDatabase.getInstance().getReference("BusLines");

        busLinesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busLines.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BusLine busLine = snapshot.getValue(BusLine.class);
                    busLines.add(busLine);

                    initializeAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeAdapter(){
        Collections.sort(busLines);
        LinesListAdapter tempAdapter = new LinesListAdapter(busLines);
        recyclerView.setAdapter(tempAdapter);
    }

}
