package com.irenbus.app.tools;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.irenbus.app.R;
import com.irenbus.app.model.BusLine;

import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class LinesListAdapter extends RecyclerView.Adapter<LinesListAdapter.TempViewHolder> {

    private List<BusLine> busLines;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    StorageReference ref;

    public LinesListAdapter(List<BusLine> busLines){
        this.busLines = busLines;
    }

    public class TempViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tvLineName;

        TempViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.LinescardView);
            tvLineName = (TextView) itemView.findViewById(R.id.tvLineTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){

                        storageReference = firebaseStorage.getInstance().getReference().child("bus-lines");
                        ref = storageReference.child(busLines.get(pos).getBusLine()+".pdf");

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Toast.makeText(v.getContext(), "Downloading timetable", Toast.LENGTH_LONG).show();
                                downloadFile(v.getContext(), busLines.get(pos).getBusLine(), ".pdf", DIRECTORY_DOWNLOADS, url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            });
        }
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    @NonNull
    @Override
    public TempViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.lines_list_item, parent, false);
        TempViewHolder tvh = new TempViewHolder(view);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TempViewHolder holder, int position) {
        holder.tvLineName.setText(busLines.get(position).getBusLine());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return busLines.size();
    }
}