package com.nebulacompanies.nebula.login;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.nebulacompanies.nebula.CustomerBooking.Utils.AppUtils;
import com.nebulacompanies.nebula.CustomerBooking.Utils.Const;
import com.nebulacompanies.nebula.CustomerBooking.Utils.UI.Activity.CustomerBookingNavigationActivity;
import com.nebulacompanies.nebula.CustomerBooking.Utils.UserAuthorization;
import com.nebulacompanies.nebula.GuestActivity;
import com.nebulacompanies.nebula.Network.APIClient;
import com.nebulacompanies.nebula.Network.APIInterface;
import com.nebulacompanies.nebula.R;
import com.nebulacompanies.nebula.broadcast.AppSignatureHashHelper;
import com.nebulacompanies.nebula.broadcast.SMSReceiver;
import com.nebulacompanies.nebula.broadcast.SmsBroadcastReceiver;
import com.nebulacompanies.nebula.util.Uttils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityOTP extends Activity implements
        SMSReceiver.OTPReceiveListener{

    public static final String TAG = ActivityOTP.class.getSimpleName();

    private SMSReceiver smsReceiver;
    BroadcastReceiver receiver;
    EditText edt_Otp1, edt_Otp2,edt_Otp3,edt_Otp4;
    String str_Username,str_Password,str_OTP,str_VerfityOTP;
    Button btn_Edit ,btn_Verify;
    ActivityOTP obj_OTP;

    private static APIInterface mAPIInterface;
    private UserAuthorization mUserAuthorization;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        obj_OTP = this;
        mUserAuthorization = new UserAuthorization(obj_OTP);
        mAPIInterface = APIClient.getClient(obj_OTP).create(APIInterface.class);
        findById();
        getBundleData();
        setAction();
        checkAndRequestPermissions();
        startSMSListener();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("OTP", "OTP");
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");
                    final String sender = intent.getStringExtra("Sender");
                    Pattern pattern = Pattern.compile("(\\d{4})");
                    Matcher matcher = pattern.matcher(message);
                    String otp = "";
                    if (matcher.find()) {
                        otp = matcher.group(0);  // 4 digit number
                    } else {

                    }
                    setOtp(otp);
                }
            }
        };

    }

    private void setOtp(String otp) {
        if (otp !=null){
            edt_Otp1.setText(""+otp.charAt(0));
            edt_Otp2.setText(""+otp.charAt(1));
            edt_Otp3.setText(""+otp.charAt(2));
            edt_Otp4.setText(""+otp.charAt(3));

            callLoginAPI(str_Username,str_Password);
        }
    }

    private void setAction() {
       btn_Edit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });
       btn_Verify.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               str_VerfityOTP = edt_Otp1.getText().toString() + edt_Otp2.getText().toString() + edt_Otp3.getText().toString() + edt_Otp4.getText().toString();

               if (str_VerfityOTP == null || str_VerfityOTP.equals("")){
                   Uttils.showToast(obj_OTP,"Please Enter OTP!");
               }else if (str_VerfityOTP.length() <4){
                   Uttils.showToast(obj_OTP,"Please Enter 4 Digit OTP!");
               }
               else {
                   if (!str_VerfityOTP.equals(str_OTP)){
                       Uttils.showToast(obj_OTP,"The OTP entered is incorrect. Please enter correct OTP or try regenerating the OTP");
                   }
                   else {
                     callLoginAPI(str_Username,str_Password);
                   }
               }
           }
       });
    }

    private void callLoginAPI() {


    }

    private void getBundleData() {
        Bundle b = getIntent().getExtras();
        if (b !=null){
          str_Username = b.getString("Username");
          str_Password = b.getString("Password");
          str_OTP = b.getString("Otp");
        }
    }

    private void findById() {
        edt_Otp1 = (EditText) findViewById(R.id.edtOTP1) ;
        edt_Otp2 = (EditText) findViewById(R.id.edtOTP2) ;
        edt_Otp3 = (EditText) findViewById(R.id.edtOTP3) ;
        edt_Otp4 = (EditText) findViewById(R.id.edtOTP4) ;

        btn_Edit = (Button) findViewById(R.id.btnCancel);
        btn_Verify = (Button) findViewById(R.id.btnLogin);

        edt_Otp1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt_Otp1.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    edt_Otp2.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void afterTextChanged(Editable s) {

            }
        });

        edt_Otp2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt_Otp2.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    edt_Otp3.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });

        edt_Otp3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt_Otp3.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    edt_Otp4.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });

        edt_Otp4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt_Otp4.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                      hideKeyboardFrom(ActivityOTP.this,edt_Otp4);
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                    Log.e("OTP","Success");
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                    Log.e("OTP","Fail");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOTPReceived(String otp) {
        showToast("OTP Received: " + otp);
        Log.e("OTP","OTP Received:");
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
        showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        showToast(error);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }


    private void showToast(String msg) {
    }


    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            int receiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
            int readSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
            }
            if (readSMS != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
                return false;
            }
            return true;
        }
        return true;

    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void callLoginAPI(String str_username, String str_password) {

        if (AppUtils.isNetworkAvailable(obj_OTP)) {
            Uttils.showProgressDialoug(obj_OTP);
            Call<JsonObject> callRequestUpdate = mAPIInterface.getUpdateToken(str_username, str_password, Const.WEB_SERVICES_PARAM.KEY_PASSWORD);
            callRequestUpdate.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful()) {
                        if (response.code() == 200 && response.body() != null) {

                            try {
                                String jsonString = response.body().toString();
                                JSONObject jsonObject = new JSONObject(jsonString);

                                if (jsonObject.has(Const.WEB_SERVICES_PARAM.KEY_TOKEN_TYPE) && jsonObject.has(Const.WEB_SERVICES_PARAM.KEY_ACCESS_TOKEN)) {
                                    String token = jsonObject.getString(Const.WEB_SERVICES_PARAM.KEY_TOKEN_TYPE) + " " + jsonObject.getString(Const.WEB_SERVICES_PARAM.KEY_ACCESS_TOKEN);
                                    mUserAuthorization.setAuthorizationToken(token);
                                    mUserAuthorization.setUserCredential(str_username, str_password, true);
                                    Intent va = new Intent(obj_OTP, CustomerBookingNavigationActivity.class);
                                    va.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(va);
                                    finish();
                                } else
                                    Toast.makeText(obj_OTP, "The ic_user name or password is incorrect.", Toast.LENGTH_LONG).show();

                                Uttils.hideProgressDialoug();
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                Uttils.hideProgressDialoug();
                                AppUtils.displayErrorMessage(obj_OTP, jsonObject.getString("error_description"));
                            } catch (Exception error) {
                                Uttils.hideProgressDialoug();
                                Log.e(getClass().getSimpleName(), "ERROR " + error.getMessage());
                            }
                        }

                    } else
                        Toast.makeText(obj_OTP, "The ic_user name or password is incorrect.", Toast.LENGTH_LONG).show();//DisplayEmptyDialog(getActivity(), -1);

                    Uttils.hideProgressDialoug();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Uttils.hideProgressDialoug();
                }
            });

        } else {
            Uttils.hideProgressDialoug();
            Toast.makeText(obj_OTP, R.string.error_msg_network, Toast.LENGTH_SHORT).show();
            //displayAlertErrorNetwork(CustomerBookingNavigationActivity.this);
        }
    }
}
