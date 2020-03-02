package com.example.bioauthentication.pin.events;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class OnItemClickListenerLockPin implements RecyclerView.OnItemTouchListener {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        int boton = e.getActionButton();
        float x = e.getX();
        float y = e.getY();
        View v = rv.getFocusedChild();
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Toast.makeText(rv.getContext(),"hola",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
