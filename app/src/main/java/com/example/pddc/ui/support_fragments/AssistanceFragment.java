package com.example.pddc.ui.support_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;


public class AssistanceFragment extends Fragment {

    String[] item = {"Suggest new feature", "Experiencing errors", "Remarks"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    MaterialButton mb_Submit;
    EditText et_emailBody, et_EmailAddress;
    String issue;

    @SuppressLint("IntentReset")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assistance, container, false);


        autoCompleteTextView = rootView.findViewById(R.id.acTextView);
        if (getParentFragment() != null) {
            adapterItems = new ArrayAdapter<>(getParentFragment().requireContext(), R.layout.list_item, item);
        }

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> issue = (String) parent.getItemAtPosition(position));

        et_EmailAddress = rootView.findViewById(R.id.etEmailAddress);
        et_emailBody = rootView.findViewById(R.id.etEmailBody);
        mb_Submit = rootView.findViewById(R.id.mbSubmit);

        mb_Submit.setOnClickListener((View v) -> {

            String senderEmail = et_EmailAddress.getText().toString().trim();
            String emailTitle = issue.trim();
            String emailBody = et_emailBody.getText().toString().trim();
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_USER, new String[]{String.valueOf(R.string.admin_email)});
            i.putExtra(Intent.EXTRA_EMAIL  , senderEmail);
            i.putExtra(Intent.EXTRA_TEXT, emailTitle);
            i.putExtra(Intent.EXTRA_SUBJECT, emailBody);
            i.setData(Uri.parse("mailto:"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(i, "Send to"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private Window getWindow() {
        return null;
    }


}