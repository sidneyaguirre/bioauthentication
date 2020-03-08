package com.example.bioauthentication.pin.adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pin.entity.LockPin;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LockPinAdapter extends RecyclerView.Adapter<LockPinAdapter.ViewHolder> {

    private List<Integer> items = Arrays.asList(1,2,3,4,5,6,7,8,9,21,0,20);
    private OnNumberClickListener onNumberClickListener;
    private OnDeleteClickListener onDeleteClickListener;

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
        Button btn = holder.btnLockPin;
        if(btn.getText().toString().equalsIgnoreCase("20")){
            btn.setTextSize(0);
            btn.setBackgroundResource(R.drawable.ic_backspace);
        }else if (btn.getText().toString().equalsIgnoreCase("21")) {
            btn.setTextSize(0);
            btn.setBackground(new ColorDrawable(Color.TRANSPARENT));
        }else {
            btn.setBackgroundResource(R.drawable.ic_background_btn);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    /**
     * esta clase es para enlazar la vista de cada item
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        public Button btnLockPin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnLockPin = itemView.findViewById(R.id.btn_item_lock_pin);
            btnLockPin.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Button b = (Button) view;
            if(onNumberClickListener!=null && event.getAction() == MotionEvent.ACTION_DOWN){
                if(b.getText().toString().equalsIgnoreCase("20")){
                    onDeleteClickListener.onDeleteClicked();
                    return true;
                }
                if(b.getText().toString().equalsIgnoreCase("21")){
                    return true;
                }
                onNumberClickListener.onNumberClicked(
                        LockPin.builder()
                                .digit(b.getText().toString())
                                .x(event.getX())
                                .y(event.getY())
                                .timeDown(event.getDownTime())
                                .timeEvent(event.getEventTime()).build());
                return true;
            }
            return false;
        }
    }

    public interface OnNumberClickListener{
        void onNumberClicked(LockPin lockPin);
    }

    public interface OnDeleteClickListener{
        void onDeleteClicked();
    }
}
