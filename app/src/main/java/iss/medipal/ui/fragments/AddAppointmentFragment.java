package iss.medipal.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import iss.medipal.MediPalApplication;
import iss.medipal.R;
import iss.medipal.constants.Constants;
import iss.medipal.dao.AppointmentDao;
import iss.medipal.dao.ReminderDao;
import iss.medipal.dao.impl.AppointmentDaoImpl;
import iss.medipal.model.Appointment;
import iss.medipal.ui.activities.MainActivity;
import iss.medipal.ui.interfaces.CustomBackPressedListener;
import iss.medipal.util.AppHelper;
import iss.medipal.util.DialogUtility;

/**
 * Created by sreekumar on 3/14/2017.
 */

public class AddAppointmentFragment extends Fragment implements CustomBackPressedListener {
    private static final String APP_STRING = "APP_STRING";
    private EditText etDate, etTime, etLocation, etDescription;
    private Button btnSave;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatterShow = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat timeFormatterSave = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());

    Calendar selectedDate = Calendar.getInstance();
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private AppointmentDao appointmentDao;
    private ReminderDao reminderDao;
    private AddAppointmentFragment.viewRefreshWhenAdded mUIUpdateListener;
    private Appointment appointment; //Parsable object
    private TextInputLayout textInputLayoutLocation;
    private boolean isEditAppointment;
    private String mAppointmentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static AddAppointmentFragment newInstance() {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        return fragment;
    }

    public static AddAppointmentFragment newInstance(Appointment appointment) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        Bundle args = new Bundle();
        args.putParcelable(APP_STRING, appointment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_add_appointment, container, false);
        findViewsById(fragmentView);
        setListeners();
        return fragmentView;
    }

    private void findViewsById(View fragmentView) {
        etLocation = (EditText) fragmentView.findViewById(R.id.et_location);
        etDate = (EditText) fragmentView.findViewById(R.id.et_appdate);
        etTime = (EditText) fragmentView.findViewById(R.id.et_appTime);
        btnSave = (Button) fragmentView.findViewById(R.id.saveApp);
        etDescription = (EditText) fragmentView.findViewById(R.id.et_description);
        etDate.setText(dateFormatter.format(selectedDate.getTime()));
        textInputLayoutLocation = (TextInputLayout) fragmentView.findViewById(R.id.tv_location);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null && getArguments().getParcelable(APP_STRING) != null) {
            appointment = (Appointment) getArguments().getParcelable(APP_STRING);
        }
        if (appointment != null) {
            updateAppointmentDetails();
            btnSave.setText("Modify Appointment");
            isEditAppointment = true;
        } else {
            appointment = new Appointment();
            isEditAppointment = false;
        }

        setListeners();
    }

    public void updateAppointmentDetails() {

        etLocation.setText(appointment.getLocation());
        textInputLayoutLocation.setHint(getString(R.string.not_editable_Location));
        etLocation.setEnabled(false);
        etDescription.setText(appointment.getDescription());
        etDate.setText(String.valueOf(dateFormatter.format(appointment.getAppointment())));
        etTime.setText(String.valueOf(timeFormatterSave.format(appointment.getAppointment())));

    }

    private void setListeners() {
        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validateAppointmentDetails()) {
                    try {
//                      int reminderId = addReminder();
//                        setReminder();
                        Appointment appointment = getAppointmentDetails();
                        if (!isEditAppointment) {
//                            appointment.setId();
                            MediPalApplication.getPersonStore().addAppointment(appointment);
                        } else {
                            MediPalApplication.getPersonStore().editAppointment(appointment);
                        }
                        Toast.makeText(getContext(), "Appointment successfully saved", Toast.LENGTH_SHORT).show();
                        Log.d("Fragment type", String.valueOf(getParentFragment()));
                        doBack();

                    } catch (Exception e) {
                        Log.d("error", e.toString());
                        Log.d("Error:", "error in add Appointment Page");
                    }
//                    onBackPressed();//Not implemeted as the screen loads only after main activity
                }
            }
        };

        View.OnClickListener appDateListner = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showDatePicker().show();
            }
        };

        View.OnClickListener appTimeListner = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showTimePicker().show();

            }
        };

        View.OnFocusChangeListener setTimeListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment timerFragment = TimePickerFragment.newInstance(Constants.REMINDER_TAB_1);
                    timerFragment.show(getChildFragmentManager(), Constants.ADD_REMINDER_DIALOG);
                }
            }
        };
        etDate.setOnClickListener(appDateListner);
        etDate.setOnFocusChangeListener(mDateFocusListener);
        etTime.setOnClickListener(appTimeListner);
        etTime.setOnFocusChangeListener(mTimeFocusListener);
        btnSave.setOnClickListener(saveListener);
        etDate.setKeyListener(null);
        etTime.setKeyListener(null);
    }

    private View.OnFocusChangeListener mTimeFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mTimePickerDialog = showTimePicker();
                mTimePickerDialog.show();
            }
        }
    };

    public boolean validateAppointmentDetails() {
        if (TextUtils.isEmpty(etLocation.getText())) {
            DialogUtility.newMessageDialog(getActivity(), getString(R.string.warning),
                    "Enter Location details").show();
            return false;
        } else if (TextUtils.isEmpty(etDate.getText())) {
            DialogUtility.newMessageDialog(getActivity(), getString(R.string.warning),
                    "Enter Appointment date").show();
            return false;
        } else if (TextUtils.isEmpty(etTime.getText())) {
            DialogUtility.newMessageDialog(getActivity(), getString(R.string.warning),
                    "Enter Appointment time").show();
            return false;
        }
        return true;
    }

    private View.OnFocusChangeListener mDateFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mDatePickerDialog = showDatePicker();
                mDatePickerDialog.show();
            }
        }
    };

    public void onBackPressed() {
        AppointmentFragment appointmentFragment = AppointmentFragment.newInstance();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.add_appointment_frame, appointmentFragment).commit();
    }

    public TimePickerDialog showTimePicker() {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int AM_PM =calendar.get(Calendar.AM_PM);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
               /* String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }*/
//                etTime.setText(hourOfDay + ":" + minute+ " " + AM_PM);
                etTime.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, false);
        return timePickerDialog;
    }

    public DatePickerDialog showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                showDate(year, month + 1, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        return datePickerDialog;
    }

    private void showDate(int year, int month, int day) {
        try {
            Date datenew = dateFormatterShow.parse(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year).toString());

            etDate.setText(dateFormatter.format(datenew));
        } catch (Exception e) {

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).setmListener(this);
        mUIUpdateListener = ((viewRefreshWhenAdded) getParentFragment());

    }

    @Override
    public void doBack() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.detach(this).commit();
            ((MainActivity) getActivity()).setmListener(null);
            if (mUIUpdateListener != null) {
                mUIUpdateListener.onAppointmentAddedUiUpdate();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String addAppointment() throws Exception {

        appointmentDao = AppointmentDaoImpl.newInstance(getActivity());
        appointmentDao.addAppointment(appointment);

        return "Appointment Added !";
    }

    public Appointment getAppointmentDetails() throws Exception {
        appointment = new Appointment();
        String description = String.valueOf(etDescription.getText());
        String location = String.valueOf(etLocation.getText());
//        String appDate = String.valueOf(etDate.getText());

        Calendar time = Calendar.getInstance();
        time.setTime(timeFormatterSave.parse(String.valueOf(etTime.getText())));

        Calendar date = Calendar.getInstance();
        date.setTime(dateFormatter.parse(String.valueOf(etDate.getText())));

        Calendar dateTime = Calendar.getInstance();
        dateTime.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        dateTime.set(Calendar.MONTH, date.get(Calendar.MONTH));
        dateTime.set(Calendar.YEAR, date.get(Calendar.YEAR));
        dateTime.set(Calendar.HOUR, time.get(Calendar.HOUR));
        dateTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        dateTime.set(Calendar.AM_PM, time.get(Calendar.AM_PM));

        try {

            appointment.setAppointment(dateTime.getTime());

        } catch (Exception ex) {

            Toast.makeText(getActivity().getApplicationContext(), "Date format exception" + ex, Toast.LENGTH_SHORT).show();
        }

        appointment.setLocation(location);
        appointment.setDescription(description);

        return appointment;

    }

    public interface viewRefreshWhenAdded {

        void onAppointmentAddedUiUpdate();
    }


}
