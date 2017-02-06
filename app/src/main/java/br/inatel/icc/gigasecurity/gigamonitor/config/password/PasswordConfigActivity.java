//package br.inatel.icc.gigasecurity.gigamonitor.config.password;
//
//import android.app.Activity;
//import android.os.AsyncTask;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//import com.xm.MyConfig;
//import com.xm.NetSdk;
//
//public class PasswordConfigActivity extends ActionBarActivity {
//
//    private Device mDevice;
//    private DeviceManager mManager;
//
//    private EditText mOldEditText, mNewEditText, mConfirmNewEditText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_password_config);
//        findViews();
//
//        mDevice = (Device) getIntent().getExtras().getSerializable("device");
//
//        mManager = DeviceManager.getInstance();
//    }
//
//    private void findViews() {
//        mOldEditText = (EditText) findViewById(R.id.edit_text_pass_actual);
//        mNewEditText = (EditText) findViewById(R.id.edit_text_pass_new);
//        mConfirmNewEditText = (EditText) findViewById(R.id.edit_text_pass_new_confirm);
//    }
//
//    private void save() {
//        final Activity activity = this;
//
//        new AsyncTask<Void, Void, Integer>() {
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                final long loginId = mDevice.getLoginID();
//                final String oldPass = mOldEditText.getText().toString();
//                final String newPass = mNewEditText.getText().toString();
//                final String confNewPass = mConfirmNewEditText.getText().toString();
//
//                if (!newPass.equals(confNewPass)) {
//                    return -34567;
//                }
//                return mManager.changePassword(loginId, oldPass, newPass);
//            }
//
//            @Override
//            protected void onPostExecute(Integer code) {
//                super.onPostExecute(code);
//                String msg;
//                switch (code) {
//                    case -34567:
//                        msg = "New passwords do not match";
//                        break;
//                    case MyConfig.ModifyPwd.SUCCESS:
//                        msg = "Password changed!";
//                        break;
//                    case -2:
//                    case MyConfig.ModifyPwd.PASSWORD_ERROR:
//                        msg = "Wrong password";
//                        break;
//                    default:
//                        msg = "Error while changing the password";
//                        msg = "Erro: "+NetSdk.getInstance().GetLastError();
//                        break;
//                }
//                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
//            }
//        }.execute();
//    }
//
//    public class ChangePsw {
//        String sUserName;
//        String sPassword;
//        String sNewPassword;
//
//        public String getsUserName() {
//            return sUserName;
//        }
//
//        public void setsUserName(String sUserName) {
//            this.sUserName = sUserName;
//        }
//
//        public String getsPassword() {
//            return sPassword;
//        }
//
//        public void setsPassword(String sPassword) {
//            this.sPassword = sPassword;
//        }
//
//        public String getsNewPassword() {
//            return sNewPassword;
//        }
//
//        public void setsNewPassword(String sNewPassword) {
//            this.sNewPassword = sNewPassword;
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.config_form, menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_config_save:
//                save();
//                Utils.hideKeyboard(this);
//                return true;
//            case android.R.id.home:
//                finish();
//                return true;
//            default:
//                return false;
//        }
//    }
//}
