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
import pt.ulisboa.tecnico.cmov.foodist.model.Language;
import pt.ulisboa.tecnico.cmov.foodist.model.Status;

public class AccountFragment extends Fragment {
    private Button saveButton;
    private int newStatus;
    private int newLang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // Statuses
        ArrayList<String> statuses = Status.getInstance(getContext());
        ArrayList<Language> languages = Language.getInstance(getContext());

        // Set to the defined values
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        TextInputLayout textInputLayout_username = root.findViewById(R.id.textInputLayout_username);
        TextInputEditText textInputEditText_username = root.findViewById(R.id.textInputEditText_username);
        TextInputLayout textInputLayout_email = root.findViewById(R.id.textInputLayout_email);
        TextInputEditText textInputEditText_email = root.findViewById(R.id.textInputEditText_email);
        AutoCompleteTextView dropdownStatus = root.findViewById(R.id.dropdown_status);
        AutoCompleteTextView dropdownLang = root.findViewById(R.id.dropdown_lang);
        saveButton = root.findViewById(R.id.button_saveAccount);

        String username = sharedPref.getString("username", "");
        String email = sharedPref.getString("email", "");
        int status = sharedPref.getInt("status", Status.DEFAULT);
        newStatus = status;
        int lang = sharedPref.getInt("lang", Language.DEFAULT);
        newLang = lang;

        textInputEditText_username.setText(username);
        textInputEditText_email.setText(email);
        dropdownStatus.setText(statuses.get(status));

        dropdownLang.setText(languages.get(lang).toString());

        // Status dropdown
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(getContext(), R.layout.layout_drop_status, statuses);
        // To fix inputmethod showing up (https://github.com/material-components/material-components-android/issues/1143)
        dropdownStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            }
        });
        dropdownStatus.setAdapter(adapterStatus);

        // Language dropdown
        LanguageAdapter adapterLang = new LanguageAdapter(getContext(), languages);
        // To fix inputmethod showing up (https://github.com/material-components/material-components-android/issues/1143)
        dropdownLang.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            }
        });
        dropdownLang.setAdapter(adapterLang);

        // Form validation
        textInputEditText_username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (textInputLayout_username.getError() != null)
                    textInputLayout_username.setError(null);
                resetSaveButton();
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
                resetSaveButton();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        dropdownStatus.setOnItemClickListener((parent, arg1, pos, id) -> {
            newStatus = pos;
            resetSaveButton();
        });

        dropdownLang.setOnItemClickListener((parent, arg1, pos, id) -> {
            newLang = pos;
            resetSaveButton();
        });

        saveButton.setOnClickListener(v -> {
            // fetch username and check if it is valid
            String newUsername = textInputEditText_username.getText().toString();
            if (!isValidUsername(newUsername))
                textInputLayout_username.setError(getString(R.string.please_enter_valid_username));

            // fetch email and check if it is valid
            String newEmail = textInputEditText_email.getText().toString();
            if (!isValidEmail(newEmail))
                textInputLayout_email.setError(getString(R.string.please_enter_valid_email));

            // status already fetched in newStatus

            // save the new values if they are valid
            if (isValidUsername(newUsername) && isValidEmail(newEmail)) {
                // if user data has changed
                if (!newUsername.equals(username) || !newEmail.equals(email) || newStatus != status || newLang != lang) {
                    sharedPref.edit().putString("username", newUsername).apply();
                    sharedPref.edit().putString("email", newEmail).apply();
                    sharedPref.edit().putInt("status", newStatus).apply();
                    if (newLang != lang) {
                        sharedPref.edit().putInt("lang", newLang).apply();
                        ((MainActivity) getActivity()).restart();
                    }
                    ((MainActivity) getActivity()).updateUser();
                    ((MainActivity) getActivity()).updateStatus();
                }
                saveButton.setText(R.string.saved);
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

    private void resetSaveButton() {
        if (saveButton.getText().equals(getString(R.string.saved)))
            saveButton.setText(R.string.save_button);
    }
}
