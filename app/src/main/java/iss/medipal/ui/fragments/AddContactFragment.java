package iss.medipal.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import iss.medipal.MediPalApplication;
import iss.medipal.R;
import iss.medipal.constants.Constants;
import iss.medipal.constants.DBConstants;
import iss.medipal.model.InCaseofEmergencyContact;
import iss.medipal.util.DialogUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {


    private AppCompatEditText mNameEdit;
    private AppCompatEditText mNumberEdit;
    private AppCompatEditText mDescriptionEdit;
    private RadioGroup mTypeGroup;
    private RadioButton mTypeSelect;
    private RadioGroup mPriorityGroup;
    private RadioButton mPrioritySelect;
    //private AppCompatEditText mPriorityEdit;
    private Button mSubmitButton;
    private InCaseofEmergencyContact mContact;
    private boolean isEdit;



    public AddContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseViews(view);
        //mTypeGroup = (RadioGroup) view.findViewById(R.id.contact_type_group);
        mTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                mTypeSelect = (RadioButton) view.findViewById(checkedId);
            }
        });
        //mPriorityGroup = (RadioGroup) view.findViewById((R.id.contact_priority_group));
        mPriorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                mPrioritySelect = (RadioButton) view.findViewById(checkedId);
            }
        });
        setListeners();
        if(getActivity().getIntent().getExtras()!=null)
        {
            Bundle bundle=getActivity().getIntent().getExtras();
            setData(bundle);
            isEdit=true;
        }
        else
            isEdit = false;
    }

    private void initialiseViews(View view)
    {
        mNameEdit = (AppCompatEditText) view.findViewById(R.id.contact_name);
        mNumberEdit = (AppCompatEditText) view.findViewById(R.id.contact_number);
        mDescriptionEdit = (AppCompatEditText) view.findViewById(R.id.contact_description);
        mTypeGroup = (RadioGroup) view.findViewById(R.id.contact_type_group);
        mTypeSelect = (RadioButton) view.findViewById(mTypeGroup.getCheckedRadioButtonId());
        mPriorityGroup = (RadioGroup) view.findViewById(R.id.contact_priority_group);
        mPrioritySelect = (RadioButton) view.findViewById(mPriorityGroup.getCheckedRadioButtonId());
        //mPriorityEdit = (AppCompatEditText) view.findViewById(R.id.contact_sequence);
        mSubmitButton = (Button) view.findViewById(R.id.submit_details_btn);
    }

    private boolean isValid(){
        if(TextUtils.isEmpty(mNameEdit.getText()) ||
                TextUtils.isEmpty(mNumberEdit.getText()) ){
            DialogUtility.newMessageDialog(getActivity(), getString(R.string.warning),
                    getString(R.string.enter_all_details_text)).show();
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber()
    {
        int numLen = mNumberEdit.getText().toString().length();
        if(numLen <5 || numLen>10)
        {
            DialogUtility.newMessageDialog(getActivity(), getString(R.string.warning),
                    "The phone number entered is invalid!").show();
            return false;
        }
        return true;
    }


    public void finish()
    {
        getActivity().finish();
    }

    private void setListeners()
    {
        mSubmitButton.setOnClickListener(mSubmitListener);
    }

    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if(isValid() && isValidPhoneNumber())
                {
                    setContactDetails();
                    /*mTypeGroup = (RadioGroup) v.findViewById(R.id.contact_type_group);
                    mPriorityGroup = (RadioGroup) v.findViewById(R.id.contact_priority_group);
                    mTypeSelect = (RadioButton) v.findViewById(mTypeGroup.getCheckedRadioButtonId());
                    Log.d("Type text",String.valueOf(mTypeSelect.getText()));
                    mPrioritySelect = (RadioButton) v.findViewById(mPriorityGroup.getCheckedRadioButtonId());*/
                    Log.d("Type text",String.valueOf(mTypeSelect.getText()));
                    switch (mTypeSelect.getText().toString()){
                        case Constants.FAMILY:
                            mContact.setContactType(Constants.FAMILY_CONTACT);
                            break;
                        case Constants.FRIEND:
                            mContact.setContactType(Constants.FRIEND_CONTACT);
                            break;
                        case Constants.DOCTOR:
                            mContact.setContactType(Constants.DOCTOR_CONTACT);
                            break;
                    }

                    switch (mPrioritySelect.getText().toString()){
                        case Constants.HIGH:
                            mContact.setSequence(Constants.HIGH_PRIORITY);
                            break;
                        case Constants.MED:
                            mContact.setSequence(Constants.MED_PRIORITY);
                            break;
                        case Constants.LOW:
                            mContact.setSequence(Constants.LOW_PRIORITY);
                            break;
                    }

                    MediPalApplication.getPersonStore().addUpdateContact(mContact,isEdit);

                    Context context = getContext();
                    CharSequence text = "Successfully saved Contact";
                    int duration = Toast.LENGTH_SHORT;

                    finish();
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            } catch (Exception e) {
                Log.d("error",e.toString());
                Log.d("Error:", "error in Add Contact Page");
            }
        }
    };


    public void setData(Bundle bundle)
    {
        InCaseofEmergencyContact contact=bundle.getParcelable(DBConstants.TABLE_ICE);
        mNameEdit.setText(contact.getContactName());
        mNumberEdit.setText(String.valueOf(contact.getContactNo()));
        mDescriptionEdit.setText(contact.getDescription());
        //mPriorityEdit.setText(String.valueOf(contact.getSequence()));

        switch (contact.getContactType())
        {
            case Constants.FAMILY_CONTACT:
                mTypeSelect = (RadioButton) mTypeGroup.getChildAt(0);
                mTypeSelect.setChecked(Boolean.TRUE);
                break;
            case Constants.FRIEND_CONTACT:
                mTypeSelect = (RadioButton) mTypeGroup.getChildAt(1);
                mTypeSelect.setChecked(Boolean.TRUE);
                break;
            case Constants.DOCTOR_CONTACT:
                mTypeSelect = (RadioButton) mTypeGroup.getChildAt(2);
                mTypeSelect.setChecked(Boolean.TRUE);
                break;
            default:
                mTypeSelect = (RadioButton) mTypeGroup.getChildAt(0);
                mTypeSelect.setChecked(Boolean.TRUE);
                break;
        }

        switch (contact.getSequence())
        {
            case Constants.HIGH_PRIORITY:
                mPrioritySelect = (RadioButton) mPriorityGroup.getChildAt(0);
                mPrioritySelect.setChecked(Boolean.TRUE);
                break;
            case Constants.MED_PRIORITY:
                mPrioritySelect = (RadioButton) mPriorityGroup.getChildAt(2);
                mPrioritySelect.setChecked(Boolean.TRUE);
                break;
            case Constants.LOW_PRIORITY:
                mPrioritySelect = (RadioButton) mPriorityGroup.getChildAt(4);
                mPrioritySelect.setChecked(Boolean.TRUE);
                break;
            default:
                mPrioritySelect = (RadioButton) mPriorityGroup.getChildAt(0);
                mPrioritySelect.setChecked(Boolean.TRUE);
                break;
        }

        mContact=new InCaseofEmergencyContact();
        mContact.setId(contact.getId());

    }


    public void setContactDetails()
    {
        if(mContact==null) {
            mContact = new InCaseofEmergencyContact();
        }
        mContact.setContactName(mNameEdit.getText().toString());
        mContact.setContactNo(Long.parseLong(mNumberEdit.getText().toString()));
        mContact.setDescription(mDescriptionEdit.getText().toString());
        //mContact.setSequence(Integer.parseInt(mPriorityEdit.getText().toString()));
    }
}
