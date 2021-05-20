package com.minz.midi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.minz.midi.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    MidiAppViewModel midiAppViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("midi.username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        midiAppViewModel.mServiceName = sp.getString("DEVICENAME","android");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        midiAppViewModel = ViewModelProviders.of(this).get(MidiAppViewModel.class);
        binding.ddnsName.setText(sp.getString("DEVICENAME","android"));
        binding.ddnsName.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    midiAppViewModel.registerService(textView.getText().toString(), 5004);
                    editor.putString("DEVICENAME",textView.getText().toString());
                    editor.apply();
                }
                return false;
            }
        });


        binding.deviceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(binding.deviceList.getSelectedItem().toString().equals("Add Custom IP")){
                    binding.editTextIp.setVisibility(View.VISIBLE);
                }
                else{
                    binding.editTextIp.setVisibility((View.GONE));
                    View viesw = getCurrentFocus();
                    if(viesw==null){
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(binding.editTextIp.getWindowToken(), 0);

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        midiAppViewModel.midiDevicesLiveData.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.midiDeviceList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        midiAppViewModel.devicesLiveData.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                List<String> newlist = new ArrayList<String>(list);
                newlist.add("Add Custom IP");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, newlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.deviceList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        binding.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Sel", "position is "+ binding.deviceList.getSelectedItemPosition());
                Log.d("Sel", "midilist pos is" + binding.midiDeviceList.getSelectedItemPosition());
                if(binding.deviceList.getSelectedItem().toString().equals("Add Custom IP")){
                    midiAppViewModel.midiDevicePos = binding.midiDeviceList.getSelectedItemPosition();
                    midiAppViewModel.manageService(-1,binding.editTextIp.getText().toString());
                }else{
                    midiAppViewModel.midiDevicePos = binding.midiDeviceList.getSelectedItemPosition();
                    midiAppViewModel.manageService(binding.deviceList.getSelectedItemPosition(),"");
                }

            }
        });
        midiAppViewModel.buttonState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.matches("Disconnect|Connecting") )binding.deviceList.setEnabled(false);
                if(s.equals("Connect"))binding.deviceList.setEnabled(true);
                binding.connectButton.setText(s);
            }
        });

    }

}