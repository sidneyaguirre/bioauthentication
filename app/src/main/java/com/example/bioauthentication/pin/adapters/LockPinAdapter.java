package com.example.bioauthentication.pin.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioauthentication.R;
import java.util.List;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LockPinAdapter extends RecyclerView.Adapter<LockPinAdapter.ViewHolder> {

    private List<Integer> items = Arrays.asList(1,2,3,4,5,6,7,8,9,0);
    private OnNumberClickListener onNumberClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lock_pin, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.btnLockPin.setText(String.valueOf(items.get(position)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * esta clase es para enlazar la vista de cada item
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Button btnLockPin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnLockPin = itemView.findViewById(R.id.btn_item_lock_pin);
            btnLockPin.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Button b = (Button) view;
            try{
                onNumberClickListener.onNumberClicked(Integer.valueOf(b.getText().toString()));
            }
            catch (Exception e){
                onNumberClickListener.onNumberClicked(-1);
            }

        }
    }

    public interface OnNumberClickListener{
        void onNumberClicked(int keyValue);
    }
}
