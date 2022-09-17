package com.nebulacompanies.nebula.login;

import static com.nebulacompanies.nebula.util.NetworkChangeReceiver.isInternetPresent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.nebulacompanies.nebula.Model.Guest.SiteProgress;
import com.nebulacompanies.nebula.Model.Login.getLoginResonse;
import com.nebulacompanies.nebula.Network.APIClient;
import com.nebulacompanies.nebula.Network.APIInterface;
import com.nebulacompanies.nebula.R;
import com.nebulacompanies.nebula.util.Uttils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends Activity {

    ActivityLogin obj_Login;

    EditText edt_BlockNo, edt_FlatNo;
    Button  btn_Cancel,btn_Continue;
    String str_Block, str_Flat,str_Username,str_Password,str_OTP;

    private APIInterface mAPIInterface;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAPIInterface = APIClient.getClient(this).create(APIInterface.class);
        obj_Login = this;
        findById();
        setAction();
    }


    private void findById() {
        edt_BlockNo = (EditText) findViewById(R.id.edtBlockId);
        edt_FlatNo = (EditText) findViewById(R.id.edtFlatNo);
        
        btn_Cancel = (Button) findViewById(R.id.btnCancel);
        btn_Continue = (Button) findViewById(R.id.btnLogin);
    }

    private void setAction() {
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_Block = edt_BlockNo.getText().toString();
                str_Flat = edt_FlatNo.getText().toString();

                if (str_Block == null || str_Block.equals("")){
                    Uttils.showToast(obj_Login,"Please Enter Block");
                }else if (str_Flat == null || str_Flat.equals("")){
                    Uttils.showToast(obj_Login,"Please Enter Flat No.");
                }
                else{
                    callLoginResponse();
                }

            }
        });
    }

    private void callLoginResponse() {
        if (isInternetPresent) {
            Uttils.showProgressDialoug(obj_Login);
            Call<getLoginResonse> wsCallingSiteProgress = mAPIInterface.getUserLogin(str_Block,str_Flat);
            wsCallingSiteProgress.enqueue(new Callback<getLoginResonse>() {
                @Override
                public void onResponse(Call<getLoginResonse> call, Response<getLoginResonse> response) {
                    Uttils.hideProgressDialoug();
                    if(response.isSuccessful()){
                        if (response.body().getStatusCode() == 1){
                          str_Username = response.body().getData().getUsername();
                          str_Password = response.body().getData().getPassword();
                          str_OTP = response.body().getData().getOTp().toString();

                          Bundle b = new Bundle();
                          b.putString("Username",str_Username);
                          b.putString("Password",str_Password);
                          b.putString("Otp",str_OTP);
                          Intent i_Otp = new Intent(obj_Login,ActivityOTP.class);
                          i_Otp.putExtras(b);
                          startActivity(i_Otp);

                        }
                        else {
                            Uttils.showToast(obj_Login,response.body().getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(Call<getLoginResonse> call, Throwable t) {
                    Uttils.hideProgressDialoug();
                    Uttils.showToast(obj_Login,t.getMessage());
                }
            });
        }
    }

}
