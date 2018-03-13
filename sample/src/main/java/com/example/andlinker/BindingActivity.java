package com.example.andlinker;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codezjx.andlinker.AndLinker;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindingActivity extends AppCompatActivity {

    private static final String REMOTE_SERVICE_PKG = "com.example.andlinker";
    public static final String REMOTE_SERVICE_ACTION = "com.example.andlinker.REMOTE_SERVICE_ACTION";

    private AndLinker mLinker;
    private IRemoteService mRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        ButterKnife.bind(this);

        mLinker = new AndLinker.Builder(this)
                .packageName(REMOTE_SERVICE_PKG)
                .action(REMOTE_SERVICE_ACTION)
                .build();
        mLinker.bind();
        mLinker.registerObject(mRemoteCallback);

        mRemoteService = mLinker.create(IRemoteService.class);
    }

    @OnClick({R.id.btn_pid, R.id.btn_basic_types, R.id.btn_callback, R.id.btn_directional, R.id.btn_oneway})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pid:
                Toast.makeText(this, "Server pid: " + mRemoteService.getPid(), Toast.LENGTH_SHORT).show();
                mRemoteService.getPid();
                break;
            case R.id.btn_basic_types:
                mRemoteService.basicTypes(1, 2L, true, 3.0f, 4.0d, "str");
                break;
            case R.id.btn_callback:
                mRemoteService.registerCallback(mRemoteCallback);
                break;
            case R.id.btn_directional:
                int[] arr = {1, 2, 3};
                ParcelableObj parcelableObj = new ParcelableObj();
                Rect rect = new Rect(10, 20, 30, 40);
                mRemoteService.directionalParamMethod(arr, parcelableObj, rect);
                Toast.makeText(this, "After directionalParamMethod parcelableObj: " + parcelableObj
                    + " rect: " + rect, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_oneway:
                mRemoteService.onewayMethod("oneway");
                Toast.makeText(this, "After oneway method client.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLinker.unbind();
    }

    private final IRemoteCallback mRemoteCallback = new IRemoteCallback() {
        @Override
        public void onValueChange(int value) {
            // Invoke when server side callback
            Toast.makeText(BindingActivity.this, "Server callback value: " + value, Toast.LENGTH_SHORT).show();
        }
    };
}
