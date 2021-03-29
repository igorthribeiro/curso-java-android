package br.com.igordev.eminjectlib.dao;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class EmFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Inject EntityManagers
        try {
            EmPrepare.prepare(getActivity(), this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
