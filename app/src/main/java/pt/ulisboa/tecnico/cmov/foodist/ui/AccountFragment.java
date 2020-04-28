package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Status;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // Statuses
        ArrayList<String> statuses = Status.getInstance(getContext());
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Status dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.layout_drop_status, statuses);
        AutoCompleteTextView dropdownStatus = root.findViewById(R.id.dropdown_status);
        // To fix inputmethod showing up (https://github.com/material-components/material-components-android/issues/1143)
        dropdownStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            }
        });

        TextInputLayout textInputLayout_username = root.findViewById(R.id.textInputLayout_username);
        TextInputEditText textInputEditText_username = root.findViewById(R.id.textInputEditText_username);
        TextInputLayout textInputLayout_email = root.findViewById(R.id.textInputLayout_email);
        TextInputEditText textInputEditText_email = root.findViewById(R.id.textInputEditText_email);

        // Set to the defined values
        textInputEditText_username.setText(sharedPref.getString("username", ""));
        textInputEditText_email.setText(sharedPref.getString("email", ""));
        dropdownStatus.setText(statuses.get(sharedPref.getInt("status", Status.DEFAULT)));
        dropdownStatus.setAdapter(adapter);

        // Form validation
        textInputEditText_username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (textInputLayout_username.getError() != null)
                    textInputLayout_username.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        textInputEditText_email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (textInputLayout_email.getError() != null)
                    textInputLayout_email.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        Button button = root.findViewById(R.id.button_saveAccount);
        button.setOnClickListener(v -> {
            // fetch username and check if it is valid
            String newUsername = textInputEditText_username.getText().toString();
            if (!isValidUsername(newUsername))
                textInputLayout_username.setError("Please enter a valid username");

            // fetch email and check if it is valid
            String newEmail = textInputEditText_email.getText().toString();
            if (!isValidEmail(newEmail))
                textInputLayout_email.setError("Please enter a valid email address");

            // fetch status
            int newStatus = statuses.indexOf(dropdownStatus.getText().toString());

            // save the new values if they are valid
            if (isValidUsername(newUsername) && isValidEmail(newEmail)) {
                sharedPref.edit().putString("username", newUsername).apply();
                sharedPref.edit().putString("email", newEmail).apply();
                sharedPref.edit().putInt("status", newStatus).apply();
            }
        });

        return root;
    }

    private boolean isValidUsername(CharSequence target) {
        return target != null && target.length() > 0;
    }

    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
